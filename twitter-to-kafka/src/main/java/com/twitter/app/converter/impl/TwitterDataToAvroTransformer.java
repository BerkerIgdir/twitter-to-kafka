package com.twitter.app.converter.impl;

import com.twitter.app.converter.DtoToAvroTransformer;
import com.twitter.app.kafka.avro.model.TwitterAvroModel;
import com.twitter.app.kafka.avro.model.User;
import com.twitter.app.streamcreator.apiconnector.dto.TwitterApiDTO;
import com.twitter.app.streamcreator.apiconnector.impl.TwitterApiStreamConnectorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class TwitterDataToAvroTransformer implements DtoToAvroTransformer<TwitterAvroModel, TwitterApiDTO.DataDto> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterDataToAvroTransformer.class);

    private final DtoToAvroTransformer<User, TwitterApiDTO.UserDto> userConverter;

    public TwitterDataToAvroTransformer(DtoToAvroTransformer<User, TwitterApiDTO.UserDto> userConverter) {
        this.userConverter = userConverter;
    }

    @Override
    public TwitterAvroModel transform(TwitterApiDTO.DataDto dto) {
        return TwitterAvroModel.newBuilder()
                .setId(dto.data().id())
                .setUserId(dto.data().userId())
                .setCreatedAt(Instant.parse(dto.data().created_at()).getEpochSecond())
                .setText(dto.data().text())
                .setUsers(dto.includes().users().stream().map(userConverter::transform).collect(Collectors.toList()))
                .build();
    }
}
