package com.twitter.elastic.test;

import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.elastic.KafkaToElasticRepo;
import com.twitter.elastic.model.TwitterIndexModel;
import com.twitter.elastic.test.converter.AvroToTwitterRecordTestImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.StreamSupport;


@SpringBootTest
@ContextConfiguration
class AvroToElasticIndexTest {
    private final TwitterAvroModel twitterAvroModel = TwitterAvroModel.newBuilder()
            .setText("test text")
            .setCreatedAt(Instant.now().getEpochSecond())
            .setUserId(1L)
            .setId(1L)
            .build();
    @Autowired
    private AvroToTwitterRecordTestImpl converter;
    @Autowired
    private KafkaToElasticRepo elasticRepo;

    @Test
    void testConvert() {
        var indexModel = converter.convert(twitterAvroModel);
        elasticRepo.save(indexModel);
        var indexedModel = StreamSupport.stream(elasticRepo.findAll().spliterator(), false).findFirst().orElseGet(null);
//        Assertions.assertEquals(indexedModel,indexModel);
    }

    @Test
    void testSave() {
        var elasticModel = new TwitterIndexModel();
        elasticModel.setCreatedAt(LocalDateTime.parse("2022-08-02T14:52:12"));
        elasticModel.setId("151234123421");
        elasticModel.setText("TEST TEXT");
        elasticModel.setUserId(123L);

        elasticRepo.save(elasticModel);
    }
}
