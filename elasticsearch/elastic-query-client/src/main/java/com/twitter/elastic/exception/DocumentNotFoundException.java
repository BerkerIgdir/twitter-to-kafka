package com.twitter.elastic.exception;

public class DocumentNotFoundException extends RuntimeException {
    private static final String DEFAULT_EXCEPTION_TEXT = "NO DOCUMENT COULD BE FOUND!";

    public DocumentNotFoundException() {
        super(DEFAULT_EXCEPTION_TEXT);
    }

    public DocumentNotFoundException(String report) {
        super(report);
    }
}
