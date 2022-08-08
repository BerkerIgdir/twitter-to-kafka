package com.twitter.elastic.query.client;

import com.twitter.elastic.model.TwitterIndexModel;

import java.util.List;
import java.util.Optional;

public interface TwitterElasticSearchQueryClient <T extends TwitterIndexModel> {
    T getTheTwitById(String id);
    List<T> getAllTweetModels();
    List<T> getTweetModelsByContent(String text);
}
