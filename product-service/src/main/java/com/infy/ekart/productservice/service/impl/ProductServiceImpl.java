package com.infy.ekart.productservice.service.impl;

import com.infy.ekart.productservice.dto.request.*;
import com.infy.ekart.productservice.dto.response.*;
import com.infy.ekart.productservice.entity.*;
import com.infy.ekart.productservice.exception.ProductNotFoundException;
import com.infy.ekart.productservice.mapper.ProductMapper;
import com.infy.ekart.productservice.repository.*;
import com.infy.ekart.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductInventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                               ProductInventoryRepository inventoryRepository,
                               CategoryRepository categoryRepository,
                               BrandRepository brandRepository,
                               ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSlug(request.getName().toLowerCase().replace(" ", "-"));
        product.setSku(request.getSku());
        product.setBasePrice(request.getBasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setThumbnailUrl(request.getThumbnailUrl());

        if (request.getCategoryId() != null) {
            categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                .ifPresent(product::setCategory);
        }

        if (request.getBrandId() != null) {
            brandRepository.findById(UUID.fromString(request.getBrandId()))
                .ifPresent(product::setBrand);
        }

        product = productRepository.save(product);

        ProductInventory inventory = new ProductInventory();
        inventory.setProduct(product);
        inventory.setQuantity(request.getInitialStock() != null ? request.getInitialStock() : 0);
        inventory.setSku(request.getSku());
        inventoryRepository.save(inventory);

        log.info("Product created: {}", product.getId());
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String productId) {
        UUID id = UUID.fromString(productId);
        Product product = productRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(String productId, UpdateProductRequest request) {
        UUID id = UUID.fromString(productId);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getBasePrice() != null) product.setBasePrice(request.getBasePrice());
        if (request.getSellingPrice() != null) product.setSellingPrice(request.getSellingPrice());
        if (request.getThumbnailUrl() != null) product.setThumbnailUrl(request.getThumbnailUrl());

        product = productRepository.save(product);
        log.info("Product updated: {}", productId);
        return productMapper.toProductResponse(product);
    }

    @Override
    public void deleteProduct(String productId) {
        UUID id = UUID.fromString(productId);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted: {}", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductListResponse getProducts(int page, int size, String category, String brand,
                                            String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAll(pageable);

        return new ProductListResponse(
            productPage.getContent().stream().map(productMapper::toProductResponse).toList(),
            productPage.getNumber(),
            productPage.getSize(),
            productPage.getTotalElements(),
            productPage.getTotalPages()
        );
    }

    @Override
    public ProductListResponse searchProducts(ProductSearchRequest request) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Product> productPage = productRepository.searchProducts(
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

    @Override
    public StockResponse getStock(String productId) {
        UUID id = UUID.fromString(productId);
        ProductInventory inventory = inventoryRepository.findByProductId(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return new StockResponse(
            inventory.getId(), inventory.getProduct().getId(),
            inventory.getQuantity(), inventory.getAvailableQuantity(),
            inventory.getStockStatus().toString()
        );
    }

    @Override
    public StockResponse updateStock(String productId, StockUpdateRequest request) {
        UUID id = UUID.fromString(productId);
        ProductInventory inventory = inventoryRepository.findByProductId(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        inventory.setQuantity(request.getQuantity());
        inventory = inventoryRepository.save(inventory);

        return new StockResponse(
            inventory.getId(), inventory.getProduct().getId(),
            inventory.getQuantity(), inventory.getAvailableQuantity(),
            inventory.getStockStatus().toString()
        );
    }
}