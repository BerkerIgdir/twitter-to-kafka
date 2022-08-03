package com.twitter.elastic.impl;

import com.twitter.elastic.KafkaToElasticRepo;
import com.twitter.elastic.KafkaToTwitterElasticClient;
import com.twitter.elastic.model.TwitterIndexModel;
import org.springframework.stereotype.Service;

@Service
public class DefaultKafkaToElasticRepoImpl implements KafkaToTwitterElasticClient<TwitterIndexModel> {
    private final KafkaToElasticRepo kafkaToElasticRepo;

    public DefaultKafkaToElasticRepoImpl(KafkaToElasticRepo kafkaToElasticRepo) {
        this.kafkaToElasticRepo = kafkaToElasticRepo;
    }

    @Override
    public void save(TwitterIndexModel document) {
        try {
            kafkaToElasticRepo.save(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
