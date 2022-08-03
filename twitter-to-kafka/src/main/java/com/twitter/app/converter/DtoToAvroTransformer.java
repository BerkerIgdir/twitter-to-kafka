package com.twitter.app.converter;

import org.apache.avro.specific.SpecificRecordBase;

public interface DtoToAvroTransformer<T extends SpecificRecordBase, K> {
    T transform(K dto);
}
