package com.infy.ekart.orderservice.mapper;

import com.infy.ekart.orderservice.dto.response.OrderItemResponse;
import com.infy.ekart.orderservice.dto.response.OrderResponse;
import com.infy.ekart.orderservice.entity.Order;
import com.infy.ekart.orderservice.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getUserId(),
            order.getUserEmail(),
            order.getUserName(),
            order.getStatus() != null ? order.getStatus().toString() : null,
            order.getSubtotal(),
            order.getDiscount(),
            order.getTax(),
            order.getShipping(),
            order.getTotalAmount(),
            order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null,
            order.getPaymentStatus(),
            order.getTrackingNumber(),
            order.getNotes(),
            order.getItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList()),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.getDeliveredAt(),
            order.getCancelledAt()
        );
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) return null;

        return new OrderItemResponse(
            item.getId(),
            item.getProductId(),
            item.getSkuId(),
            item.getProductName(),
            item.getImageUrl(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotalPrice()
        );
    }
}