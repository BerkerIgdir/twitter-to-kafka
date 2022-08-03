package com.twitter.elastic.test.converter;

import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.elastic.converter.AvroToElasticIndexConverter;
import com.twitter.elastic.model.TwitterIndexModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
@Component
@Profile("test")
public class AvroToTwitterRecordTestImpl implements AvroToElasticIndexConverter<TwitterAvroModel, TwitterIndexModel> {

    public AvroToTwitterRecordTestImpl(){
        System.out.println("TEST CONVERTER IS BEING LOADED!");
    }
    @Override
    public TwitterIndexModel convert(TwitterAvroModel avroModel) {
        var localDateTime= Instant.ofEpochSecond(avroModel.getCreatedAt()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return new TwitterIndexModel(String.valueOf(avroModel.getUserId()), avroModel.getId(), avroModel.getText(), localDateTime);
    }
}