package com.twitter.app.retry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {
    @Bean
    WebClient.Builder webClientBuilderBean() {
        return WebClient.builder();
    }
}
