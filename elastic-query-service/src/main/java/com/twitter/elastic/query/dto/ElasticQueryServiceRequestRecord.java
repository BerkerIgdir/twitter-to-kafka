package com.twitter.elastic.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public record ElasticQueryServiceRequestRecord(@JsonProperty(value = "text") @NotEmpty String text) {
}
