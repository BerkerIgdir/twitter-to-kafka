package com.twitter.elastic.query.service;

import com.twitter.elastic.query.dto.ElasticQueryServiceResponseModel;

import java.util.List;

public interface ElasticSearchQueryService {
     List<ElasticQueryServiceResponseModel> getAllTweetModels();
     List<ElasticQueryServiceResponseModel> getTweetsByText(String text);
}
