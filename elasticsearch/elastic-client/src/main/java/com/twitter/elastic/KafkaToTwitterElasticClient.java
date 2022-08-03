package com.twitter.elastic;

import com.twitter.elastic.model.TwitterIndexModel;

public interface KafkaToTwitterElasticClient<T extends TwitterIndexModel> {
    void save(T document);
}
