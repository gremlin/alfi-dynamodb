package com.example.alfidynamodb.controller;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.alfidynamodb.persistence.GetItemRequester;
import com.gremlin.GremlinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Main {

    private final GetItemRequester getItemRequester;

    public Main(@Autowired GetItemRequester getItemRequester) {
        this.getItemRequester = getItemRequester;
    }

    @GetMapping("/")
    public @ResponseBody ResponseEntity<String> hello() {
        return new ResponseEntity<>("Welcome to the ALFI DynamoDB example! visit /{id} to fetch an item from the DynamoDB table.", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<Map<String, AttributeValue>> fetchItems(@PathVariable String id) {
        return new ResponseEntity<>(getItemRequester.getItem(id), HttpStatus.OK);
    }
}
