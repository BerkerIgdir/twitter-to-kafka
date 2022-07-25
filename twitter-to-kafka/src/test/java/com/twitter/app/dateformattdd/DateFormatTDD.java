package com.twitter.app.dateformattdd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;


//@ExtendWith(SpringExtension.class)
class DateFormatTDD {
    private final static String SAMPLE_DATE_TO_PARSE = "2022-07-23T18:44:49.000Z";
//    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void parseDate(){
        var date = Instant.parse(SAMPLE_DATE_TO_PARSE);
        Assertions.assertNotNull(date);
    }
}
