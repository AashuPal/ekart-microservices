package com.infy.ekart.orderservice.dto.response;

import java.util.List;

public class OrderListResponse {
    private List<OrderResponse> orders;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public OrderListResponse(List<OrderResponse> orders, int page, int size,
                             long totalElements, int totalPages) {
        this.orders = orders;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<OrderResponse> getOrders() { return orders; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}