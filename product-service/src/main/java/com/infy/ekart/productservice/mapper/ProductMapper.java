package com.infy.ekart.productservice.mapper;

import com.infy.ekart.productservice.dto.response.ProductResponse;
import com.infy.ekart.productservice.dto.response.StockResponse;
import com.infy.ekart.productservice.entity.Product;
import com.infy.ekart.productservice.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product product) {
        if (product == null) return null;

        StockResponse stockResponse = null;
        if (product.getInventory() != null) {
            stockResponse = new StockResponse(
                product.getInventory().getId(),
                product.getId(),
                product.getInventory().getQuantity(),
                product.getInventory().getAvailableQuantity(),
                product.getInventory().getStockStatus().toString()
            );
        }

        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getSlug(),
            product.getSku(),
            product.getBasePrice(),
            product.getSellingPrice(),
            product.getDiscountPercentage(),
            product.getCategory() != null ? product.getCategory().getName() : null,
            product.getBrand() != null ? product.getBrand().getName() : null,
            product.getStatus().toString(),
            product.getThumbnailUrl(),
            product.getAverageRating(),
            product.getReviewCount(),
            product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList()),
            stockResponse,
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}