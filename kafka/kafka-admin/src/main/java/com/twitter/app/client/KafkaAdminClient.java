package com.twitter.app.client;

import com.twitter.app.config.KafkaConfigProperties;
import com.twitter.app.config.RetryConfigProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaAdminClient {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);
    private final RetryConfigProperties retryConfig;
    private final RetryTemplate retryTemplate;
    private final AdminClient adminClient;
    private final KafkaConfigProperties kafkaConfigData;

    private final WebClient webClient;

    public KafkaAdminClient(RetryConfigProperties retryConfig, RetryTemplate retryTemplate, AdminClient adminClient, WebClient.Builder webClientBuilder, KafkaConfigProperties kafkaConfigData) {
        this.retryConfig = retryConfig;
        this.retryTemplate = retryTemplate;
        this.adminClient = adminClient;
        this.kafkaConfigData = kafkaConfigData;

        this.webClient = webClientBuilder.baseUrl(kafkaConfigData.getSchemaRegistryUrl()).build();
    }


    public void createAvroSchema() throws InterruptedException {
        int maxRetry = retryConfig.getMaxAttempts();
        int currentRetry = 1;
        long sleepTime = retryConfig.getSleepTimeMs();
        double multiplier = retryConfig.getMultiplier();
        while (Boolean.FALSE    .equals(retryTemplate.execute(this::retryCreateAvroSchema))) {
            if (currentRetry > maxRetry) {
                throw new RuntimeException();
            }

            currentRetry++;
            Thread.sleep(sleepTime);
            sleepTime *= multiplier;
        }
        retryTemplate.execute(this::retryCreateAvroSchema);
    }

    private boolean retryCreateAvroSchema(RetryContext retryContext) {

        LOG.info("Creating avro schema, retry attempt:{}", retryContext.getRetryCount());

        return Boolean.TRUE.equals(webClient.get()
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
                .onStatus(HttpStatus::is5xxServerError, ClientResponse::createException)
                .toBodilessEntity()
                .map(e -> e.getStatusCode() == HttpStatus.OK)
                .block());
    }

    public void createTopics() {
        retryTemplate.execute(this::retryCreateTopics);
        try {
            checkCreateTopics();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void checkCreateTopics() throws InterruptedException {
        int maxRetry = retryConfig.getMaxAttempts();
        int currentRetry = 1;
        long sleepTime = retryConfig.getSleepTimeMs();
        double multiplier = retryConfig.getMultiplier();
        while (!isTopicsCreated()) {
            if (currentRetry > maxRetry) {
                throw new RuntimeException();
            }
            retryTemplate.execute(this::retryCreateTopics);
            currentRetry++;
            Thread.sleep(sleepTime);
            sleepTime *= multiplier;
        }
    }

    private CreateTopicsResult retryCreateTopics(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        LOG.info("Creating {} topics(s), attempt {}", topicNames.size(), retryContext.getRetryCount());
        List<NewTopic> kafkaTopics = topicNames.stream().map(this::createTopic).toList();
        return adminClient.createTopics(kafkaTopics);
    }

    private boolean isTopicsCreated() {
        return retryTemplate.execute(this::retryIsTopicsCreated);
    }

    private boolean retryIsTopicsCreated(RetryContext retryContext) {

        LOG.info("Checking if the topic/s are created... retry number: {}", retryContext.getRetryCount());
        try {
            return adminClient.listTopics()
                    .names()
                    .get()
                    .containsAll(kafkaConfigData.getTopicNamesToCreate());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private NewTopic createTopic(String topicName) {
        return new NewTopic(
                topicName.trim(),
                kafkaConfigData.getNumOfPartitions(),
                kafkaConfigData.getReplicationFactor());
    }
}
