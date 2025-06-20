package com.firomsa.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review", description = "API for managing reviews")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @Operation(summary = "For getting all reviews")
    @GetMapping()
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getAll();
        return ResponseEntity.ok().body(reviews);
    }

    @Operation(summary = "For getting a single review")
    @GetMapping(path = "/{id}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable int id) {
        ReviewResponseDTO review = reviewService.get(id);
        return ResponseEntity.ok().body(review);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "For deleting a review")
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        reviewService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
