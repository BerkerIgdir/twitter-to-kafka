package com.twitter.test.tdd.propertiestdd;

import com.twitter.test.config.TestConfigClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = TestConfigClass.class)
class MapOutOfPropertiesFileTDD {
    @Autowired
    private MapOutOfPropertyLoaderTest loaderTest;

    @Test
    void loadProperty() {
        var supposedToMap = loaderTest.getKafkaConsumerConfig();
        Assertions.assertNotEquals(0, supposedToMap.size());
    }
}
