package com.twitter.elastic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class KafkaToElasticDTO {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ElasticUserDto(@JsonProperty("id") long id,
                                 @JsonProperty("username") String userName,
                                 @JsonProperty("name") String name) implements Serializable { }
}
