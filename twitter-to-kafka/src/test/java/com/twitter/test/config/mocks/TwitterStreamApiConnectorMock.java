package com.twitter.test.config.mocks;

import com.twitter.app.streamcreator.apiconnector.TwitterApiStreamConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class TwitterStreamApiConnectorMock implements TwitterApiStreamConnector {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamApiConnectorMock.class);
    @Override
    public void connectStream() {
        LOG.info("MOCKED CONNECTOR STARTS");
    }
}
