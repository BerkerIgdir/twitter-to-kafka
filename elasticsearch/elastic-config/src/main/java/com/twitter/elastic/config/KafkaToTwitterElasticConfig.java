package com.twitter.elastic.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.twitter.elastic")
public class KafkaToTwitterElasticConfig extends AbstractElasticsearchConfiguration {
     private final ElasticSearchConfigProperties elasticSearchConfigProperties;

    public KafkaToTwitterElasticConfig(ElasticSearchConfigProperties elasticSearchConfigProperties) {
        this.elasticSearchConfigProperties = elasticSearchConfigProperties;
    }

    @Override
    public RestHighLevelClient elasticsearchClient() {
        UriComponents serverUri = UriComponentsBuilder.fromHttpUrl(elasticSearchConfigProperties.getConnectionUrl()).build();
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(
                        Objects.requireNonNull(serverUri.getHost()),
                        serverUri.getPort(),
                        serverUri.getScheme()
                )).setRequestConfigCallback(
                        requestConfigBuilder ->
                                requestConfigBuilder
                                        .setConnectTimeout(elasticSearchConfigProperties.getConnectTimeoutMs())
                                        .setSocketTimeout(elasticSearchConfigProperties.getSocketTimeoutMs())
                )
        );
    }
    @Bean
    public ElasticsearchOperations elasticsearchOps() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
