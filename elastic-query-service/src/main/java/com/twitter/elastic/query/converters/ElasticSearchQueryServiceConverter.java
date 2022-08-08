package com.twitter.elastic.query.converters;

import com.twitter.elastic.model.TwitterIndexModel;
import com.twitter.elastic.query.dto.ElasticQueryServiceResponseModel;

public interface ElasticSearchQueryServiceConverter<T extends TwitterIndexModel, K extends ElasticQueryServiceResponseModel> {
    K convert(T t);
}
