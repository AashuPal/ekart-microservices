package com.infy.ekart.orderservice.service;

import com.infy.ekart.orderservice.dto.request.UpdateOrderStatusRequest;
import com.infy.ekart.orderservice.dto.response.OrderListResponse;
import com.infy.ekart.orderservice.dto.response.OrderResponse;

import java.util.Map;

public interface OrderService {

    OrderResponse createOrder(Map<String, Object> request);

    OrderResponse getOrderById(String orderId);

    OrderListResponse getUserOrders(String userId, int page, int size);

    OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);

    OrderResponse cancelOrder(String orderId);

    OrderResponse getOrderByNumber(String orderNumber);
}