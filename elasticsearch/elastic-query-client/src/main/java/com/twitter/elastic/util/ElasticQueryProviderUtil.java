package com.twitter.elastic.util;

import org.elasticsearch.index.query.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class ElasticQueryProviderUtil {

    public Query getMatchAllQuery() {
        return new NativeSearchQueryBuilder()
                .withQuery(new MatchAllQueryBuilder())
                .build();
    }

    public Query getMatchByIdQuery(String id) {
        return new NativeSearchQueryBuilder()
                .withFilter(new TermQueryBuilder("id",id))
                .build();
    }

    public Query getMatchByFieldQuery(String field, String text) {
        return new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder().must(QueryBuilders.matchQuery(field,text)))
                .build();
    }
}
