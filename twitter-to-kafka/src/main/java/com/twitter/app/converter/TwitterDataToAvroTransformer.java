package com.twitter.app.converter;

import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.app.streamcreator.apiconnector.impl.TwitterApiStreamConnectorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TwitterDataToAvroTransformer implements DtoToAvroTransformer<TwitterAvroModel, TwitterApiStreamConnectorImpl.DataDto> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterDataToAvroTransformer.class);

    @Override
    public TwitterAvroModel transform(TwitterApiStreamConnectorImpl.DataDto dto) {
        return TwitterAvroModel.newBuilder()
                .setId(dto.data().id())
                .setUserId(dto.data().userId())
                .setCreatedAt(Instant.parse(dto.data().created_at()).getEpochSecond())
                .setText(dto.data().text())
                .build();
    }
}
