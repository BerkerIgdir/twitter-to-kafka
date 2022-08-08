package com.twitter.elastic.query.controller;

import com.twitter.elastic.query.dto.ElasticQueryServiceResponseModel;
import com.twitter.elastic.query.service.ElasticSearchQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ElasticSearchQueryServiceRestController {
    private final ElasticSearchQueryService queryService;

    public ElasticSearchQueryServiceRestController(ElasticSearchQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/match-all")
    ResponseEntity<List<ElasticQueryServiceResponseModel>> matchAll() {
        return ResponseEntity.ok(queryService.getAllTweetModels());
    }
}
