package com.twitter.app.transformer;

import org.apache.avro.specific.SpecificRecordBase;

public interface TwitterAppTransformer<T extends SpecificRecordBase, K> {
    T transform(K dto);
}
