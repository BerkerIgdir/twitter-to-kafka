package com.twitter.elastic.query.security.validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Qualifier("demo-realm-client-aud-validator")
public class ElasticQueryAudienceValidator implements OAuth2TokenValidator<Jwt> {
    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
       return Optional.ofNullable(token)
                .map(JwtClaimAccessor::getAudience)
                .filter(aud -> aud.contains("demo-realm-client"))
                .map(aud -> OAuth2TokenValidatorResult.success())
                .orElseGet(() -> {
                    var error = new OAuth2Error("invalid_token", "The required audience is missing!", null);
                    return OAuth2TokenValidatorResult.failure(error);
                });
    }
}
