package com.twitter.test.tdd.propertiestdd;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("test")
@ConfigurationProperties(prefix = "test")
public class MapOutOfPropertyLoaderTest {
    private Map<String, String> kafkaConsumerConfig = new HashMap<>();

    public void setKafkaConsumerConfig(Map<String, String> kafkaConsumerConfig) {
        this.kafkaConsumerConfig = kafkaConsumerConfig;
    }

    public Map<String, String> getKafkaConsumerConfig() {
        return kafkaConsumerConfig;
    }
}
