package com.example.alfidynamodb.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.gremlin.*;
import com.gremlin.aws.GremlinDynamoRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlfiConfig {

    private static final String APPLICATION_QUERY_NAME = "ALFIDemoApplication";

    @Value("${aws.region}")
    private String region;


    public GremlinCoordinatesProvider gremlinCoordinatesProvider() {
        return new GremlinCoordinatesProvider() {
            @Override
            public ApplicationCoordinates initializeApplicationCoordinates() {
                return new ApplicationCoordinates.Builder()
                        .withType(APPLICATION_QUERY_NAME)
                        .build();
            }
        };
    }

    public GremlinServiceFactory gremlinServiceFactory() {
        return new GremlinServiceFactory(gremlinCoordinatesProvider());
    }

    public GremlinService gremlinService() {
        return gremlinServiceFactory().getGremlinService();
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        final RequestHandler2 gremlinDynamoInterceptor = new GremlinDynamoRequestInterceptor(gremlinService(), 1500, 500);
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .withClientConfiguration(new ClientConfiguration()
                        .withClientExecutionTimeout(1500)
                        .withConnectionTimeout(500)
                        .withMaxErrorRetry(2)
                )
                .withRequestHandlers(gremlinDynamoInterceptor).build();
    }

}
