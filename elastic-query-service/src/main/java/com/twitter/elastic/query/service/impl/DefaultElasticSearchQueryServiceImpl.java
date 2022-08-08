package com.twitter.elastic.query.service.impl;

import com.twitter.elastic.model.TwitterIndexModel;
import com.twitter.elastic.query.client.TwitterElasticSearchQueryClient;
import com.twitter.elastic.query.converters.ElasticSearchQueryServiceConverter;
import com.twitter.elastic.query.dto.ElasticQueryServiceResponseModel;
import com.twitter.elastic.query.service.ElasticSearchQueryService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;

@Service
public class DefaultElasticSearchQueryServiceImpl implements ElasticSearchQueryService {

    private final TwitterElasticSearchQueryClient<TwitterIndexModel> elasticSearchQueryClient;
    private final ElasticSearchQueryServiceConverter<TwitterIndexModel, ElasticQueryServiceResponseModel> twitterModelIndexToQueryServiceResponseConverter = twitterIndexModel ->
            new ElasticQueryServiceResponseModel(twitterIndexModel.getId(),
                    twitterIndexModel.getUserId(), twitterIndexModel.getText(),
                    twitterIndexModel.getCreatedAt().atZone(ZoneId.systemDefault()));

    public DefaultElasticSearchQueryServiceImpl(TwitterElasticSearchQueryClient<TwitterIndexModel> elasticSearchQueryClient) {
        this.elasticSearchQueryClient = elasticSearchQueryClient;
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllTweetModels() {
       return elasticSearchQueryClient.getAllTweetModels()
               .stream()
               .map(twitterModelIndexToQueryServiceResponseConverter::convert)
               .toList();
    }
}
