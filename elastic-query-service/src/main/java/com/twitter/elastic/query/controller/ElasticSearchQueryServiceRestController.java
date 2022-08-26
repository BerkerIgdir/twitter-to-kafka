package com.twitter.elastic.query.controller;

import com.twitter.elastic.query.dto.ElasticQueryServiceRequestRecord;
import com.twitter.elastic.query.dto.ElasticQueryServiceResponseModel;
import com.twitter.elastic.query.service.ElasticSearchQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class ElasticSearchQueryServiceRestController {
    private final ElasticSearchQueryService queryService;

    public ElasticSearchQueryServiceRestController(ElasticSearchQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/match-all")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> matchAll() {
        return ResponseEntity.ok(queryService.getAllTweetModels());
    }

    @GetMapping("/match-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> matchByText(@Valid @RequestParam("text") ElasticQueryServiceRequestRecord requestRecord) {
        return ResponseEntity.ok(queryService.getTweetsByText(requestRecord.text()));
    }
}
