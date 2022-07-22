package com.twitter.app.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;

@EnableRetry
@Configuration
public class KafkaAdminConfig {

    private final KafkaConfigProperties configProperties;

    public KafkaAdminConfig(KafkaConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configProperties.getBootstrapServers()));
    }
}
