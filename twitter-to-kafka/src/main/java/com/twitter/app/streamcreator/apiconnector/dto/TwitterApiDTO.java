package com.twitter.app.streamcreator.apiconnector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class TwitterApiDTO {
    private TwitterApiDTO(){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TwitterDto(@JsonProperty("id") long id,
                             @JsonProperty("author_id") long userId,
                             @JsonProperty("created_at") String created_at,
                             @JsonProperty("text") String text) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserDto(@JsonProperty("id") long id,
                          @JsonProperty("username") String userName,
                          @JsonProperty("name") String name) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IncludedDto(List<UserDto> users) { }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DataDto(TwitterDto data, IncludedDto includes) { }

}
