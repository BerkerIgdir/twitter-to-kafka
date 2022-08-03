package com.twitter.test.tdd.httpclienttests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.channel.ChannelOption;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;


import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;

@SpringBootTest
class WebClientTDD {
    private static final Logger LOG = LoggerFactory.getLogger(WebClientTDD.class);
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TwitterDto(@JsonProperty("id") long id, @JsonProperty("author_id") long userId,
                              @JsonProperty("created_at") String createdAt, @JsonProperty("text") String text) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record dataDto(TwitterDto data){}
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final String TWITTER_BASE_STREAM_URL = "https://api.twitter.com/2/tweets/search/stream";
    private static final String RULES_URI = "/rules";
    private static final String ADD_RULES_BODY_TEMPLATE = """
            {"add" : [%s]}
            """;
    private static final String DELETE_RULES_BODY_TEMPLATE = """
            {
            	"delete": {
            		"values": [%s]
            	}
            }
            """;
    private final HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(30))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
    private WebClient webClient;

    @PostConstruct
    void forOncePreparations() {
        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(TWITTER_BASE_STREAM_URL)
                .defaultHeader("Authorization", String.format("Bearer %s", bearerToken))
                .build();
    }

    @Value("${twitter-app.bearer.token}")
    private String bearerToken;

    @Test
    synchronized void webClientStreamTest() throws InterruptedException {
        deleteStreamRule();
        createStreamRule();
        webClient.get()
                .uri(getFilteredStreamUri())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
                .bodyToFlux(String.class)
                .map(str -> {
                    try {
                        return objectMapper.readValue(str, TwitterDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribe(dto -> LOG.info(dto.toString()));

        wait();
    }

    private URI getFilteredStreamUri() {
        return UriComponentsBuilder.fromUri(URI.create(TWITTER_BASE_STREAM_URL))
                .queryParam("tweet.fields","created_at,public_metrics")
                .build()
                .toUri();
    }

    private void createStreamRule() {

        var stringBody = String.format(ADD_RULES_BODY_TEMPLATE, """
                       {
                           "value": "Spring",
                           "tag": "Keyword: Spring"
                       }  ,
                       {
                           "value": "Berker",
                           "tag": "Keyword: Berker"
                       }   ,
                       {
                           "value": "Java",
                           "tag": "Keyword: Java"
                       }            
                """);
        LOG.info(stringBody);
        rulesEndPointPost(stringBody);
    }

    private void deleteStreamRule() {
        var stringBody = String.format(DELETE_RULES_BODY_TEMPLATE,  """  
                                                                            "Berker","Spring", "Java"
                                                                            """);
        LOG.info(stringBody);
        rulesEndPointPost(stringBody);
    }

    private void rulesEndPointPost(String stringBody) {
        var result = webClient.post()
                .uri(RULES_URI)
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
