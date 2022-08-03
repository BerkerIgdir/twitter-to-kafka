package com.twitter.elastic;

import com.twitter.elastic.model.TwitterIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface KafkaToElasticRepo extends ElasticsearchRepository<TwitterIndexModel, String> {
}
