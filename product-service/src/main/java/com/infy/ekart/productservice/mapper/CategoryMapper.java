package com.infy.ekart.productservice.mapper;

import com.infy.ekart.productservice.dto.response.CategoryResponse;
import com.infy.ekart.productservice.entity.Category;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryResponse toCategoryResponse(Category category) {
        if (category == null) return null;

        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getSlug(),
            category.getDescription(),
            category.getImageUrl(),
            category.getParent() != null ? category.getParent().getId() : null,
            category.getSubCategories().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList()),
            category.getSortOrder()
        );
    }
}