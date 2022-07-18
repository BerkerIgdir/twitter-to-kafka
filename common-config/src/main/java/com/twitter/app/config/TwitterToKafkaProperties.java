package com.twitter.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "twitter-app")
public class TwitterToKafkaProperties {
    private List<String> keywords;
    private List<String> fields;
    private String bearerToken;
    private String twitterStreamUrl;

    public List<String> getKeywords() {
        return Collections.unmodifiableList(keywords);
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getTwitterStreamUrl() {
        return twitterStreamUrl;
    }

    public void setTwitterStreamUrl(String twitterStreamUrl) {
        this.twitterStreamUrl = twitterStreamUrl;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
