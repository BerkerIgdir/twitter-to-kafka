package com.twitter.elastic.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record ElasticQueryServiceResponseModel(@JsonProperty(value = "id") String id,
                                               @JsonProperty(value = "user_id") Long userId,
                                               @JsonProperty(value = "text") String text,
                                               @JsonProperty(value = "created_at") ZonedDateTime createdAt) { }
