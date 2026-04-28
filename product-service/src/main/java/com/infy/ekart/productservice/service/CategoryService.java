package com.infy.ekart.productservice.service;

import com.infy.ekart.productservice.dto.request.CreateCategoryRequest;
import com.infy.ekart.productservice.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(String categoryId);

    CategoryResponse updateCategory(String categoryId, CreateCategoryRequest request);

    void deleteCategory(String categoryId);
}