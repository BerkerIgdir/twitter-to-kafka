package com.twitter.elastic.test.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
@EnableAutoConfiguration
@ComponentScan(value = "com.twitter.elastic")
public class TestConfigClass {
    public TestConfigClass(){
        System.out.println("com.twitter.elastic.test.config TEST CONFIG CLASS IS BEING LOADED");
    }
}
