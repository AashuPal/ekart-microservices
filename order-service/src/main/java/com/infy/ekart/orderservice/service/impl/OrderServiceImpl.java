package com.infy.ekart.orderservice.service.impl;

import com.infy.ekart.orderservice.dto.request.UpdateOrderStatusRequest;
import com.infy.ekart.orderservice.dto.response.OrderItemResponse;
import com.infy.ekart.orderservice.dto.response.OrderListResponse;
import com.infy.ekart.orderservice.dto.response.OrderResponse;
import com.infy.ekart.orderservice.entity.*;
import com.infy.ekart.orderservice.enums.OrderStatus;
import com.infy.ekart.orderservice.enums.PaymentMethod;
import com.infy.ekart.orderservice.exception.OrderNotFoundException;
import com.infy.ekart.orderservice.repository.OrderRepository;
import com.infy.ekart.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder(Map<String, Object> request) {
        log.info("Creating new order");

        Order order = new Order();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setUserId(UUID.fromString((String) request.get("userId")));
        order.setUserEmail((String) request.getOrDefault("email", ""));
        order.setUserName((String) request.getOrDefault("name", ""));
        order.setStatus(OrderStatus.PENDING);

        // Set amounts
        order.setSubtotal(new BigDecimal(request.getOrDefault("subtotal", "0").toString()));
        order.setDiscount(new BigDecimal(request.getOrDefault("discount", "0").toString()));
        order.setTax(new BigDecimal(request.getOrDefault("tax", "0").toString()));
        order.setShipping(new BigDecimal(request.getOrDefault("shipping", "0").toString()));
        order.setTotalAmount(new BigDecimal(request.getOrDefault("totalAmount", "0").toString()));

        // Payment method
        String paymentMethod = (String) request.getOrDefault("paymentMethod", "COD");
        order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));

        // Set notes
        order.setNotes((String) request.getOrDefault("notes", ""));

        // Set dates
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Add items
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
        if (items != null) {
            for (Map<String, Object> itemData : items) {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProductId(UUID.fromString((String) itemData.get("productId")));
                item.setSkuId(UUID.fromString((String) itemData.get("skuId")));
                item.setProductName((String) itemData.get("productName"));
                item.setImageUrl((String) itemData.get("imageUrl"));
                item.setQuantity(Integer.parseInt(itemData.get("quantity").toString()));
                item.setUnitPrice(new BigDecimal(itemData.get("unitPrice").toString()));
                item.setTotalPrice(new BigDecimal(itemData.get("totalPrice").toString()));
                order.getItems().add(item);
            }
        }

        // Shipping Address
        Map<String, Object> shipAddr = (Map<String, Object>) request.get("shippingAddress");
        if (shipAddr != null) {
            ShippingAddress address = new ShippingAddress();
            address.setFullName((String) shipAddr.get("fullName"));
            address.setAddressLine1((String) shipAddr.get("addressLine1"));
            address.setAddressLine2((String) shipAddr.get("addressLine2"));
            address.setCity((String) shipAddr.get("city"));
            address.setState((String) shipAddr.get("state"));
            address.setPostalCode((String) shipAddr.get("postalCode"));
            address.setCountry((String) shipAddr.get("country"));
            address.setPhoneNumber((String) shipAddr.get("phoneNumber"));
            order.setShippingAddress(address);
        }

        order = orderRepository.save(order);
        log.info("Order created: {}", order.getOrderNumber());
        return toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId) {
        UUID id = UUID.fromString(orderId);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        return toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderListResponse getUserOrders(String userId, int page, int size) {
        UUID uid = UUID.fromString(userId);
        Page<Order> orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(
            uid, PageRequest.of(page, size));

        return new OrderListResponse(
            orderPage.getContent().stream().map(this::toOrderResponse).collect(Collectors.toList()),
            orderPage.getNumber(),
            orderPage.getSize(),
            orderPage.getTotalElements(),
            orderPage.getTotalPages()
        );
    }

    @Override
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        UUID id = UUID.fromString(orderId);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        order.setStatus(OrderStatus.valueOf(request.getStatus()));
        order.setTrackingNumber(request.getTrackingNumber());
        order.setUpdatedAt(LocalDateTime.now());

        if (request.getStatus().equals("DELIVERED")) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, request.getStatus());
        return toOrderResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(String orderId) {
        UUID id = UUID.fromString(orderId);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);
        log.info("Order {} cancelled", orderId);
        return toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return toOrderResponse(order);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getId(), item.getProductId(), item.getSkuId(),
                item.getProductName(), item.getImageUrl(),
                item.getQuantity(), item.getUnitPrice(), item.getTotalPrice()
            ))
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getUserId(),
            order.getUserEmail(),
            order.getUserName(),
            order.getStatus().toString(),
            order.getSubtotal(),
            order.getDiscount(),
            order.getTax(),
            order.getShipping(),
            order.getTotalAmount(),
            order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null,
            order.getPaymentStatus(),
            order.getTrackingNumber(),
            order.getNotes(),
            itemResponses,
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.getDeliveredAt(),
            order.getCancelledAt()
        );
    }
}