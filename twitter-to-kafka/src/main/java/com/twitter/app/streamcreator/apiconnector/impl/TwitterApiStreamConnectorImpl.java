package com.twitter.app.streamcreator.apiconnector.impl;

import com.twitter.app.config.TwitterToKafkaProperties;
import com.twitter.app.streamcreator.apiconnector.TwitterApiStreamConnector;
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

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.util.stream.Collectors;

import static com.twitter.app.streamcreator.apiconnector.TwitterApiConstants.*;

@Service
public class TwitterApiStreamConnectorImpl implements TwitterApiStreamConnector {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterApiStreamConnectorImpl.class);

    private final TwitterToKafkaProperties properties;
    private final HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(30))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
    private final WebClient webClient;

    public TwitterApiStreamConnectorImpl(TwitterToKafkaProperties properties, WebClient.Builder webClientBuilder) {
        this.properties = properties;
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
        webClient.get()
                .uri(getFilteredStreamUri())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
                .bodyToFlux(String.class)
                .subscribe(LOG::info);
    }

    private URI getFilteredStreamUri() {
        return UriComponentsBuilder.fromUri(URI.create(properties.getTwitterStreamUrl()))
                .queryParam(TWITTER_STREAM_FIELDS_QUERY_PARAM, String.join(",", properties.getFields()))
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
        var requestBody = String.format(DELETE_RULES_BODY_TEMPLATE, String.join(",", properties.getKeywords()));

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
