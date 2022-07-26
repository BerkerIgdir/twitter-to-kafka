package com.twitter.elastic.client;

import org.apache.avro.specific.SpecificRecordBase;

public interface TwitterApiConsumerClient<T extends SpecificRecordBase> {
    void listen(T recordBase);
}
