package com.twitter.elastic.converter.impl;

import com.twitter.app.kafka.avro.model.User;
import com.twitter.elastic.converter.AvroToElasticIndexConverter;
import com.twitter.elastic.model.KafkaToElasticDTO;
import org.springframework.stereotype.Component;

@Component
public class UserAvroToElasticUserConverterImpl implements AvroToElasticIndexConverter<User, KafkaToElasticDTO.ElasticUserDto> {
    @Override
    public KafkaToElasticDTO.ElasticUserDto convert(User avroModel) {
        return new KafkaToElasticDTO.ElasticUserDto(avroModel.getId(),avroModel.getUserName(),avroModel.getName());
    }
}
