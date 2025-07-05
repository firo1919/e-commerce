package com.firomsa.ecommerce.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.mapper.ReviewMapper;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.repository.ReviewRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ReviewResponseDTO> getAll() {
        return reviewRepository.findAll().stream().map(ReviewMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ReviewResponseDTO get(int id) {
        Review image = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review: " + id));
        return ReviewMapper.toDTO(image);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(int id) {
        Review image = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review: " +
                        id));
        reviewRepository.delete(image);
    }
}
