package com.twitter.elastic.consumer;

import com.twitter.elastic.config.KafkaConfigPropertiesForConsumer;
import com.twitter.elastic.config.KafkaConsumerProperties;
import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.client.TwitterApiConsumerClient;
import com.twitter.elastic.KafkaToTwitterElasticClient;
import com.twitter.elastic.converter.AvroToElasticIndexConverter;
import com.twitter.elastic.model.TwitterIndexModel;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class TwitterIndexConsumer implements TwitterApiConsumerClient<TwitterAvroModel> {

    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final KafkaConfigPropertiesForConsumer kafkaConfigPropertiesForConsumer;
    private final KafkaToTwitterElasticClient<TwitterIndexModel> kafkaToTwitterElasticClient;
    private final AvroToElasticIndexConverter<TwitterAvroModel, TwitterIndexModel> toElasticIndexConverter;

    public TwitterIndexConsumer(KafkaConsumerProperties kafkaConsumerProperties,
                                KafkaConfigPropertiesForConsumer kafkaConfigPropertiesForConsumer,
                                KafkaToTwitterElasticClient<TwitterIndexModel> kafkaToTwitterElasticClient,
                                AvroToElasticIndexConverter<TwitterAvroModel, TwitterIndexModel> toElasticIndexConverter) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaConfigPropertiesForConsumer = kafkaConfigPropertiesForConsumer;
        this.kafkaToTwitterElasticClient = kafkaToTwitterElasticClient;
        this.toElasticIndexConverter = toElasticIndexConverter;
    }

    @Override
    @KafkaListener(id = "#{kafkaConsumerProperties.consumerGroupId}", topics = "#{kafkaConfigProperties.topicName}")
    public void listen(TwitterAvroModel recordBase) {
        kafkaToTwitterElasticClient.save(toElasticIndexConverter.convert(recordBase));
    }
}
