package com.infy.ekart.productservice.service;

import com.infy.ekart.productservice.dto.request.CreateReviewRequest;
import com.infy.ekart.productservice.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(String productId, CreateReviewRequest request);

    List<ReviewResponse> getReviewsByProductId(String productId);

    void deleteReview(String reviewId);
}