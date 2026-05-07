package com.infy.ekart.productservice.mapper;

import com.infy.ekart.productservice.dto.response.ReviewResponse;
import com.infy.ekart.productservice.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toReviewResponse(Review review) {
        if (review == null) return null;

        return new ReviewResponse(
            review.getId(),
            review.getProduct().getId(),
            review.getUserId(),
            review.getUserName(),
            review.getRating(),
            review.getTitle(),
            review.getComment(),
            review.getIsVerifiedPurchase(),
            review.getHelpfulCount(),
            review.getCreatedAt()
        );
    }
}