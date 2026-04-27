package com.infy.ekart.cartservice.mapper;

import com.infy.ekart.cartservice.dto.response.CartItemResponse;
import com.infy.ekart.cartservice.entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartItemMapper {

    public CartItemResponse toCartItemResponse(CartItem item) {
        if (item == null) return null;

        return new CartItemResponse(
            item.getId(),
            item.getProductId(),
            item.getSkuId(),
            item.getProductName(),
            item.getImageUrl(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotalPrice(),
            item.getAddedAt(),
            item.getUpdatedAt()
        );
    }

    public List<CartItemResponse> toCartItemResponseList(List<CartItem> items) {
        if (items == null) return List.of();

        return items.stream()
            .map(this::toCartItemResponse)
            .collect(Collectors.toList());
    }
}