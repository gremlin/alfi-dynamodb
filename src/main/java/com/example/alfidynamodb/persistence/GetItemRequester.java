package com.example.alfidynamodb.persistence;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.kms.model.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GetItemRequester {

    private final Logger LOG = LoggerFactory.getLogger(getClass().getName());

    @Value("${dynamo.db.table}")
    private String table;

    private final AmazonDynamoDB amazonDynamoDB;

    public GetItemRequester(@Autowired AmazonDynamoDB amazonDynamoDB) {
        this.amazonDynamoDB = amazonDynamoDB;
    }

    public Map<String, AttributeValue> getItem(String id) {
        long startTime = System.currentTimeMillis();
        try {

            LOG.info(String.format("Querying DynamoDB for item with ID %s...", id));
            Map<String, AttributeValue> returnedItem = amazonDynamoDB.getItem(createRequestWithId(id)).getItem();
            if (returnedItem != null) {
                return returnedItem;
            } else {
                throw new NotFoundException(String.format("Item with id %s not found!", id));
            }
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            LOG.info(String.format("Call to DynamoDB took %s milliseconds.", duration));
        }
    }

    private GetItemRequest createRequestWithId(String id) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("id", new AttributeValue(id));
        return new GetItemRequest().withKey(keyToGet).withTableName(table);
    }
}
