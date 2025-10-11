package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.v1.dto.ReviewRequestDTO;
import com.firomsa.ecommerce.v1.dto.ReviewResponseDTO;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponseDTO toDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId().toString())
                .productId(review.getProduct().getId().toString())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt().toString())
                .build();
    }

    public static Review toModel(ReviewRequestDTO reviewRequestDTO) {
        return Review.builder()
                .rating(reviewRequestDTO.getRating())
                .comment(reviewRequestDTO.getComment())
                .build();
    }
}
