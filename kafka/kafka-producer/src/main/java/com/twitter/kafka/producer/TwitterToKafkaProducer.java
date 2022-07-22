package com.twitter.kafka.producer;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

public interface TwitterToKafkaProducer<K extends Serializable, T extends SpecificRecordBase> {
    void send(String topicName,K key, T message);
}
