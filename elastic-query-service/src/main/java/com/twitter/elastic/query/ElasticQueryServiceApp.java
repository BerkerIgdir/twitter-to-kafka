package com.twitter.elastic.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.twitter.elastic")
public class ElasticQueryServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryServiceApp.class);
    }
}
