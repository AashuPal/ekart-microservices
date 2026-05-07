package com.infy.ekart.productservice.controller;

import com.infy.ekart.productservice.dto.request.ProductSearchRequest;
import com.infy.ekart.productservice.dto.response.ProductListResponse;
import com.infy.ekart.productservice.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<ProductListResponse> search(@RequestBody ProductSearchRequest request) {
        ProductListResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }
}