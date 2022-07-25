package com.twitter.app.transformer;

import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.app.streamcreator.apiconnector.impl.TwitterApiStreamConnectorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Component
public class TwitterDataToAvroTransformer implements TwitterAppTransformer<TwitterAvroModel, TwitterApiStreamConnectorImpl.DataDto> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterDataToAvroTransformer.class);

    @Override
    public TwitterAvroModel transform(TwitterApiStreamConnectorImpl.DataDto dto) {
        var optionalDateString = Optional.ofNullable(dto.data().created_at()).orElseGet(Instant.MAX::toString);

        Instant date;
        try {
            date = Instant.parse(optionalDateString);
        } catch (DateTimeParseException e) {
            LOG.error("The date could not be parsed is:{}", optionalDateString);
            date = Instant.now();
        }

        return TwitterAvroModel.newBuilder()
                .setId(dto.data().id())
                .setUserId(dto.data().userId())
                .setCreatedAt(date.getEpochSecond())
                .setText(dto.data().text())
                .build();
    }
}
