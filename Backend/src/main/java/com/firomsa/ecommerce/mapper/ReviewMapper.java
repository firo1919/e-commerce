package com.firomsa.ecommerce.mapper;

import com.firomsa.ecommerce.dto.ReviewRequestDTO;
import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.model.Review;

public class ReviewMapper {
    public static ReviewResponseDTO toDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId().toString())
                .productId(review.getProduct().getId().toString())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt().toString())
                .updatedAt(review.getUpdatedAt().toString())
                .build();
    }

    public static Review toModel(ReviewRequestDTO reviewRequestDTO){
        return Review.builder()
                .rating(reviewRequestDTO.getRating())
                .comment(reviewRequestDTO.getComment())
                .build();
    }
}
