package com.infy.ekart.productservice.service.impl;

import com.infy.ekart.productservice.dto.request.CreateCategoryRequest;
import com.infy.ekart.productservice.dto.response.CategoryResponse;
import com.infy.ekart.productservice.entity.Category;
import com.infy.ekart.productservice.exception.CategoryNotFoundException;
import com.infy.ekart.productservice.mapper.CategoryMapper;
import com.infy.ekart.productservice.repository.CategoryRepository;
import com.infy.ekart.productservice.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                                CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category: {}", request.getName());

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getName().toLowerCase().replace(" ", "-"));
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());

        if (request.getParentId() != null) {
            categoryRepository.findById(UUID.fromString(request.getParentId()))
                .ifPresent(category::setParent);
        }

        category = categoryRepository.save(category);
        log.info("Category created: {}", category.getId());
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrder().stream()
            .map(categoryMapper::toCategoryResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(String categoryId) {
        UUID id = UUID.fromString(categoryId);
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(String categoryId, CreateCategoryRequest request) {
        UUID id = UUID.fromString(categoryId);
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getImageUrl() != null) category.setImageUrl(request.getImageUrl());

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public void deleteCategory(String categoryId) {
        UUID id = UUID.fromString(categoryId);
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted: {}", categoryId);
    }
}