package com.twitter.kafka.producer.impl;

import com.twitter.app.config.KafkaConfigProperties;
import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.kafka.producer.TwitterToKafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class TwitterToKafkaProducerImpl implements TwitterToKafkaProducer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaProducerImpl.class);
    private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterToKafkaProducerImpl(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOG.debug("Topic:{}, Key:{}, Message: {}", topicName, key, message.getText());
        var listenableFuture = kafkaTemplate.send(topicName, key, message);
        addCallback(topicName, message, listenableFuture);
    }

    private void addCallback(String topicName, TwitterAvroModel message,
                             ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
            }

            @Override
            public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                LOG.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime());
            }
        });
    }

}
