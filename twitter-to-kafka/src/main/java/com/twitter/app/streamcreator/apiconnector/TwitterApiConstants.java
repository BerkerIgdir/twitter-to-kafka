package com.twitter.app.streamcreator.apiconnector;
public class TwitterApiConstants {
    public static final String RULES_URI = "/rules";
    public static final String TWITTER_STREAM_FIELDS_QUERY_PARAM = "tweet.fields";
    public static final String ADD_RULES_BODY_TEMPLATE = """
            {"add" : [%s]}
            """;
    public static final String RULES_INNER_BODY_TEMPLATE = """
            {
                "value": "%s",
                "tag": "Keyword: %s"
            }""";
    public static final String DELETE_RULES_BODY_TEMPLATE = """
            {
            	"delete": {
            		"values": ["%s"]
            	}
            }""";
}
