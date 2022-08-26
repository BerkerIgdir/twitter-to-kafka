package com.twitter.elastic.query.security.config;

import com.twitter.elastic.query.config.UserConfigData;
import com.twitter.elastic.query.controller.exceptionhandler.ElasticSearchQueryControllerAdvice;
import com.twitter.elastic.query.security.converter.JwtToValidTokenConverter;
import com.twitter.elastic.query.security.validator.ElasticQueryAudienceValidator;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserConfigData userDataConfig;
    private final JwtToValidTokenConverter jwtToValidTokenConverter;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    private final ElasticQueryAudienceValidator audienceValidator;
    private final ElasticSearchQueryControllerAdvice elasticSearchQueryControllerAdvice;

    public WebSecurityConfig(UserConfigData userDataConfig,
                             JwtToValidTokenConverter jwtToValidTokenConverter,
                             OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
                             ElasticQueryAudienceValidator audienceValidator,
                             ElasticSearchQueryControllerAdvice elasticSearchQueryControllerAdvice) {
        this.userDataConfig = userDataConfig;
        this.jwtToValidTokenConverter = jwtToValidTokenConverter;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.audienceValidator = audienceValidator;
        this.elasticSearchQueryControllerAdvice = elasticSearchQueryControllerAdvice;
    }

    @Override
    public void configure(HttpSecurity security) throws Exception {
        security.exceptionHandling()
                .authenticationEntryPoint(elasticSearchQueryControllerAdvice)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtToValidTokenConverter)
                .and();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(
                oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(
                        oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        OAuth2TokenValidator<Jwt> withAudience =
                new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }
}
