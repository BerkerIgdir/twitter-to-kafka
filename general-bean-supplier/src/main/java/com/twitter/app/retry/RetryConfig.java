package com.twitter.app.retry;

import com.twitter.app.config.RetryConfigProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {
    private final RetryConfigProperties retryConfigProperties;

    public RetryConfig(RetryConfigProperties retryConfigProperties) {
        this.retryConfigProperties = retryConfigProperties;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        var retryTemplate = new RetryTemplate();

        var backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retryConfigProperties.getInitialIntervalMs());
        backOffPolicy.setMultiplier(retryConfigProperties.getMultiplier());
        backOffPolicy.setMaxInterval(retryConfigProperties.getMaxIntervalMs());

        retryTemplate.setBackOffPolicy(backOffPolicy);

        var retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retryConfigProperties.getMaxAttempts());

        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
