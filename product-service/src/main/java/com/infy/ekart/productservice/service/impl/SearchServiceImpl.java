package com.infy.ekart.productservice.service.impl;

import com.infy.ekart.productservice.dto.request.ProductSearchRequest;
import com.infy.ekart.productservice.dto.response.ProductListResponse;
import com.infy.ekart.productservice.mapper.ProductMapper;
import com.infy.ekart.productservice.repository.ProductRepository;
import com.infy.ekart.productservice.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public SearchServiceImpl(ProductRepository productRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductListResponse search(ProductSearchRequest request) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<com.infy.ekart.productservice.entity.Product> productPage = 
            productRepository.searchProducts(
                request.getKeyword(),
                request.getCategoryId() != null ? UUID.fromString(request.getCategoryId()) : null,
                request.getBrandId() != null ? UUID.fromString(request.getBrandId()) : null,
                request.getMinPrice(),
                request.getMaxPrice(),
                pageable
            );

        return new ProductListResponse(
            productPage.getContent().stream().map(productMapper::toProductResponse).toList(),
            productPage.getNumber(),
            productPage.getSize(),
            productPage.getTotalElements(),
            productPage.getTotalPages()
        );
    }
}