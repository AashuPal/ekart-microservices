package com.infy.ekart.productservice.service.impl;

import com.infy.ekart.productservice.dto.request.CreateReviewRequest;
import com.infy.ekart.productservice.dto.response.ReviewResponse;
import com.infy.ekart.productservice.entity.Product;
import com.infy.ekart.productservice.entity.Review;
import com.infy.ekart.productservice.exception.ProductNotFoundException;
import com.infy.ekart.productservice.mapper.ReviewMapper;
import com.infy.ekart.productservice.repository.ProductRepository;
import com.infy.ekart.productservice.repository.ReviewRepository;
import com.infy.ekart.productservice.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                              ProductRepository productRepository,
                              ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public ReviewResponse addReview(String productId, CreateReviewRequest request) {
        UUID id = UUID.fromString(productId);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        Review review = new Review();
        review.setProduct(product);
        review.setUserId(UUID.fromString(request.getUserId()));
        review.setUserName(request.getUserName());
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        // Update product rating
        Double avgRating = reviewRepository.getAverageRatingByProductId(id);
        Integer reviewCount = reviewRepository.getReviewCountByProductId(id);
        product.setAverageRating(avgRating);
        product.setReviewCount(reviewCount);
        productRepository.save(product);

        log.info("Review added for product: {}", productId);
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProductId(String productId) {
        UUID id = UUID.fromString(productId);
        return reviewRepository.findByProductId(id, org.springframework.data.domain.Pageable.unpaged())
            .getContent().stream()
            .map(reviewMapper::toReviewResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(String reviewId) {
        UUID id = UUID.fromString(reviewId);
        reviewRepository.deleteById(id);
        log.info("Review deleted: {}", reviewId);
    }
}