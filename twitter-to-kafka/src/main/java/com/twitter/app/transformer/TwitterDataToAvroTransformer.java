package com.twitter.app.transformer;

import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.app.streamcreator.apiconnector.impl.TwitterApiStreamConnectorImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class TwitterDataToAvroTransformer  implements TwitterAppTransformer<TwitterAvroModel, TwitterApiStreamConnectorImpl.DataDto> {

    @Override
    public TwitterAvroModel transform(TwitterApiStreamConnectorImpl.DataDto dto) {
        var date = LocalDateTime.parse(dto.data().created_at(),DateTimeFormatter.ISO_INSTANT).toInstant(ZoneOffset.UTC);
        return TwitterAvroModel.newBuilder()
                .setId(dto.data().id())
                .setUserId(dto.data().userId())
                .setCreatedAt(date.getEpochSecond())
                .build();
    }
}
