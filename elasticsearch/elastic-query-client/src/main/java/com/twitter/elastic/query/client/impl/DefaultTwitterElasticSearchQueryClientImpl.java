package com.twitter.elastic.query.client.impl;

import com.twitter.elastic.exception.DocumentNotFoundException;
import com.twitter.elastic.model.TwitterIndexModel;
import com.twitter.elastic.query.client.TwitterElasticSearchQueryClient;
import com.twitter.elastic.util.ElasticQueryProviderUtil;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


@Service
public class DefaultTwitterElasticSearchQueryClientImpl implements TwitterElasticSearchQueryClient<TwitterIndexModel> {

    private static final String TEXT_FIELD_NAME = "text";
    private final ElasticQueryProviderUtil queryProviderUtil;
    private final ElasticsearchOperations elasticsearchOperations;

    public DefaultTwitterElasticSearchQueryClientImpl(ElasticQueryProviderUtil queryProviderUtil, ElasticsearchOperations elasticsearchOperations) {
        this.queryProviderUtil = queryProviderUtil;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public TwitterIndexModel getTheTwitById(String id) {
        var result = elasticsearchOperations.searchOne(queryProviderUtil.getMatchByIdQuery(id), TwitterIndexModel.class);
        return Optional.ofNullable(result).map(SearchHit::getContent).orElseThrow(DocumentNotFoundException::new);
    }

    @Override
    public List<TwitterIndexModel> getAllTweetModels() {
        return getResults(queryProviderUtil::getMatchAllQuery, TwitterIndexModel.class);
    }

    @Override
    public List<TwitterIndexModel> getTweetModelsByContent(String text) {
        return getResults(() -> queryProviderUtil.getMatchByFieldQuery(TEXT_FIELD_NAME, text), TwitterIndexModel.class);
    }

    private <T extends TwitterIndexModel> List<T> getResults(Supplier<Query> functionalInterface, Class<T> clazz) {
        return elasticsearchOperations.search(functionalInterface.get(), clazz)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

}
