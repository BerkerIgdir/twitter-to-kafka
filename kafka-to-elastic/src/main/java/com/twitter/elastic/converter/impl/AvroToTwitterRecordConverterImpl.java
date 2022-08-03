package com.twitter.elastic.converter.impl;

import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.elastic.converter.AvroToElasticIndexConverter;
import com.twitter.elastic.model.TwitterIndexRecord;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class AvroToTwitterRecordConverterImpl implements AvroToElasticIndexConverter<TwitterAvroModel, TwitterIndexRecord> {
    @Override
    public TwitterIndexRecord convert(TwitterAvroModel avroModel) {
        var localDateTime= Instant.ofEpochMilli(avroModel.getCreatedAt()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return new TwitterIndexRecord(String.valueOf(avroModel.getUserId()), avroModel.getId(), avroModel.getText(), localDateTime);
    }
}
