package com.twitter.app.converter.impl;

import com.twitter.app.converter.DtoToAvroTransformer;
import com.twitter.app.kafka.avro.model.User;
import com.twitter.app.streamcreator.apiconnector.dto.TwitterApiDTO;
import org.springframework.stereotype.Component;

@Component
public class TwitterDataToUserAvroTransformer implements DtoToAvroTransformer<User, TwitterApiDTO.UserDto> {
    @Override
    public User transform(TwitterApiDTO.UserDto dto) {
        return User.newBuilder()
                .setUserName(dto.userName())
                .setName(dto.name())
                .setId(dto.id())
                .build();
    }
}
