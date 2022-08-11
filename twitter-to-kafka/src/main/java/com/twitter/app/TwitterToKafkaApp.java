package com.twitter.app;

import com.twitter.app.client.KafkaAdminClient;
import com.twitter.app.config.KafkaConfigProperties;
import com.twitter.app.config.TwitterToKafkaProperties;
import com.twitter.app.streamcreator.apiconnector.TwitterApiStreamConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan(basePackages = "com.twitter")
public class TwitterToKafkaApp implements CommandLineRunner {

    Logger LOG = LoggerFactory.getLogger(TwitterToKafkaApp.class);

    private final TwitterApiStreamConnector twitterApiStreamConnector;
    private final KafkaAdminClient kafkaAdminClient;
    private final TwitterToKafkaProperties properties;
    public TwitterToKafkaApp(TwitterApiStreamConnector twitterApiStreamConnector, KafkaAdminClient kafkaAdminClient, TwitterToKafkaProperties properties) {
        this.twitterApiStreamConnector = twitterApiStreamConnector;
        this.kafkaAdminClient = kafkaAdminClient;
        this.properties = properties;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaApp.class);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("The application starts up...");
        LOG.info("THE BEARER TOKEN IS:{}",properties.getBearerToken());
        kafkaAdminClient.createTopics();
        kafkaAdminClient.createAvroSchema();
        twitterApiStreamConnector.connectStream();
    }

    @EventListener
    public void start(ApplicationStartedEvent event){
        Arrays.stream(event.getApplicationContext().getBeanDefinitionNames()).forEach(LOG::info);
    }

}
