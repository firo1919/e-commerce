package com.firomsa.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.mapper.ReviewMapper;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.repository.ReviewRepository;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewResponseDTO> getAll() {
        return reviewRepository.findAll().stream().map(ReviewMapper::toDTO).toList();
    }

    public ReviewResponseDTO get(int id) {
        Review image = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review: " + id));
        return ReviewMapper.toDTO(image);
    }

    public void remove(int id) {
        Review image = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review: " +
                        id));
        reviewRepository.delete(image);
    }
}
