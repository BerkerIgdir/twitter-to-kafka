package com.twitter.elastic.converter;

import com.twitter.elastic.model.TwitterIndexModel;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

public interface AvroToElasticIndexConverter<T extends SpecificRecordBase, K extends Serializable> {
    K convert(T avroModel);
}
