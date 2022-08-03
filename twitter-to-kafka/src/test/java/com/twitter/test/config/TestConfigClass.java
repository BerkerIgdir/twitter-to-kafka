package com.twitter.test.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile("test")
@Configuration
@ComponentScan(value = "com.twitter.test")
@EnableAutoConfiguration
@EnableConfigurationProperties
public class TestConfigClass {
}
