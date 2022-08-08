package com.twitter.elastic.query.dto;

import java.time.ZonedDateTime;

public record ElasticQueryServiceResponseModel(String id, Long userId, String text, ZonedDateTime createdAt) { }
