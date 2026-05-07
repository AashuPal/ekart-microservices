package com.infy.ekart.productservice.dto.response;

import java.util.List;

public class ProductListResponse {

    private List<ProductResponse> products;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public ProductListResponse(List<ProductResponse> products, int page, int size,
                               long totalElements, int totalPages) {
        this.products = products;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    // Getters
    public List<ProductResponse> getProducts() { return products; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}