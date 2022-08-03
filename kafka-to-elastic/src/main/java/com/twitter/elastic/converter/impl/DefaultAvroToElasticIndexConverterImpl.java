package com.twitter.elastic.converter.impl;

import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.elastic.converter.AvroToElasticIndexConverter;
import com.twitter.elastic.model.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class DefaultAvroToElasticIndexConverterImpl implements AvroToElasticIndexConverter<TwitterAvroModel, TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAvroToElasticIndexConverterImpl.class);

    @Override
    public TwitterIndexModel convert(TwitterAvroModel avroModel) {
        var localDateTime = Instant.ofEpochSecond(avroModel.getCreatedAt()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return new TwitterIndexModel(String.valueOf(avroModel.getUserId()), avroModel.getId(), avroModel.getText(), localDateTime);
    }
}
