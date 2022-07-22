package com.twitter.app.streamcreator.apiconnector.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.twitter.app.config.KafkaConfigProperties;
import com.twitter.app.config.TwitterToKafkaProperties;
import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.app.streamcreator.apiconnector.TwitterApiStreamConnector;
import com.twitter.app.transformer.TwitterAppTransformer;
import com.twitter.kafka.producer.TwitterToKafkaProducer;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.stream.Collectors;

import static com.twitter.app.streamcreator.apiconnector.TwitterApiConstants.*;

@Service
public class TwitterApiStreamConnectorImpl implements TwitterApiStreamConnector {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterApiStreamConnectorImpl.class);

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TwitterDto(@JsonProperty("id") long id, @JsonProperty("author_id") long userId,
                             @JsonProperty("created_at") String created_at, @JsonProperty("text") String text) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DataDto(TwitterDto data) {
    }

    private final DataDto JACKSON_FAIL_DTO_RESPONSE = new DataDto(new TwitterDto(Long.MAX_VALUE, Long.MAX_VALUE, "", ""));

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final TwitterToKafkaProperties properties;
    private final KafkaConfigProperties kafkaConfigData;
    private final WebClient webClient;
    private final TwitterToKafkaProducer<Long, TwitterAvroModel> kafkaProducer;

    private final TwitterAppTransformer<TwitterAvroModel,DataDto> dataDtoTwitterAppTransformer;

    public TwitterApiStreamConnectorImpl(TwitterToKafkaProperties properties, KafkaConfigProperties kafkaConfigData, WebClient.Builder webClientBuilder, TwitterToKafkaProducer<Long, TwitterAvroModel> kafkaProducer, TwitterAppTransformer<TwitterAvroModel, DataDto> dataDtoTwitterAppTransformer) {
        this.properties = properties;
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducer = kafkaProducer;
        this.dataDtoTwitterAppTransformer = dataDtoTwitterAppTransformer;
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ((int) Duration.ofSeconds(30).toMillis()));
        webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.getTwitterStreamUrl())
                .defaultHeader("Authorization", String.format("Bearer %s", properties.getBearerToken()))
                .build();
    }

    @Override
    public void connectStream() {
        deleteStreamRule();
        createStreamRule();
        var topicName = kafkaConfigData.getTopicName();
        webClient.get()
                .uri(getFilteredStreamUri())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
                .bodyToFlux(String.class)
                .map(this::fromStringToDTO)
                .subscribe(dto -> kafkaProducer.send(topicName, dto.data().id, dataDtoTwitterAppTransformer.transform(dto)));
    }

    private DataDto fromStringToDTO(String str) {
        try {
            return objectMapper.readValue(str, DataDto.class);
        } catch (JsonProcessingException e) {
            LOG.info("the string can not be converted is:{}", str);
            return JACKSON_FAIL_DTO_RESPONSE;
        }
    }


    private URI getFilteredStreamUri() {
        return UriComponentsBuilder.fromUri(URI.create(properties.getTwitterStreamUrl()))
                .queryParam(TWITTER_STREAM_FIELDS_QUERY_PARAM, String.join(",", properties.getFields()))
                .queryParam(TWITTER_STREAM_EXPANSIONS_QUERY_PARAM, String.join(",", properties.getExpansions()))
                .build()
                .toUri();
    }

    private void createStreamRule() {
        var innerRequestBody = properties.getKeywords()
                .stream()
                .map(str -> String.format(RULES_INNER_BODY_TEMPLATE, str, str))
                .collect(Collectors.joining(","));

        var requestBody = String.format(ADD_RULES_BODY_TEMPLATE, innerRequestBody);
        LOG.info(requestBody);
        rulesEndPointPost(requestBody);
    }

    private void deleteStreamRule() {
        var innerBody = properties.getKeywords()
                .stream()
                .map(s -> "\"".concat(s).concat("\""))
                .collect(Collectors.joining(","));

        var requestBody = String.format(DELETE_RULES_BODY_TEMPLATE, innerBody);

        LOG.info(requestBody);
        rulesEndPointPost(requestBody);
    }

    private void rulesEndPointPost(String stringBody) {
        var result = webClient.post()
                .uri(properties.getTwitterStreamUrl().concat(RULES_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(stringBody))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
                .bodyToMono(String.class)
                .block();

        LOG.info(result);
    }
}
