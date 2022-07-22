package com.twitter.app;

//import com.twitter.app.client.KafkaAdminClient;
import com.twitter.app.streamcreator.apiconnector.TwitterApiStreamConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.twitter")
public class TwitterToKafkaApp implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(TwitterToKafkaApp.class);

    private final TwitterApiStreamConnector twitterApiStreamConnector;
//    private final KafkaAdminClient kafkaAdminClient;
    public TwitterToKafkaApp(TwitterApiStreamConnector twitterApiStreamConnector) {
        this.twitterApiStreamConnector = twitterApiStreamConnector;
//        this.kafkaAdminClient = kafkaAdminClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaApp.class);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("The application starts up...");
        twitterApiStreamConnector.connectStream();
    }
}
