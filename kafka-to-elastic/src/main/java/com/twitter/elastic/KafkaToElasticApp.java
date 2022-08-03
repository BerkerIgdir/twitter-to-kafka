package com.twitter.elastic;

import com.twitter.app.client.KafkaAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.util.Arrays;

@SpringBootApplication
@ComponentScan("com.twitter")
public class KafkaToElasticApp implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaToElasticApp.class);

    private final KafkaAdminClient kafkaAdminClient;

    public KafkaToElasticApp(KafkaAdminClient kafkaAdminClient) {
        this.kafkaAdminClient = kafkaAdminClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaToElasticApp.class);
    }

    @Override
    public void run(String... args) throws Exception {
        while (!kafkaAdminClient.isTopicsCreated()){
            Thread.sleep(Duration.ofSeconds(5).toMillis());
        }
        LOG.info("The kafka to elastic application is starting up...");
    }

    @EventListener
    public void onStart(ApplicationStartedEvent event){
        LOG.info("The loaded beans are:");
        Arrays.stream(event.getApplicationContext().getBeanDefinitionNames()).forEach(LOG::info);
    }

}
