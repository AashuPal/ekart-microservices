package com.infy.ekart.productservice.controller;

import com.infy.ekart.productservice.dto.request.*;
import com.infy.ekart.productservice.dto.response.*;
import com.infy.ekart.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        ProductListResponse response = productService.getProducts(page, size, null, null, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<ProductListResponse> searchProducts(@RequestBody ProductSearchRequest request) {
        ProductListResponse response = productService.searchProducts(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/stock")
    public ResponseEntity<StockResponse> getStock(@PathVariable String productId) {
        StockResponse response = productService.getStock(productId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<StockResponse> updateStock(
            @PathVariable String productId,
            @Valid @RequestBody StockUpdateRequest request) {
        StockResponse response = productService.updateStock(productId, request);
        return ResponseEntity.ok(response);
    }
}