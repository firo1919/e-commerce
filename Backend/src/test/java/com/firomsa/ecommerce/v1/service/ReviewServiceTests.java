package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.ReviewRepository;
import com.firomsa.ecommerce.v1.dto.ReviewResponseDTO;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private User user;
    private Product product;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .description("A test product")
                .price(100.0)
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        user = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        review = Review.builder()
                .id(1)
                .rating(5)
                .comment("Great")
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void ReviewService_GetAll_ReturnsReviews() {
        // Arrange
        given(reviewRepository.findAll()).willReturn(List.of(review));

        // Act
        List<ReviewResponseDTO> result = reviewService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    public void ReviewService_Get_ReturnsReview() {
        // Arrange
        given(reviewRepository.findById(1)).willReturn(Optional.of(review));

        // Act
        ReviewResponseDTO result = reviewService.get(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(review.getId());
        verify(reviewRepository, times(1)).findById(1);
    }

    @Test
    public void ReviewService_Get_Throws_WhenNotFound() {
        // Arrange
        given(reviewRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.get(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Review: 1");
    }

    @Test
    public void ReviewService_Remove_DeletesReview() {
        // Arrange
        given(reviewRepository.findById(1)).willReturn(Optional.of(review));

        // Act
        reviewService.remove(1);

        // Assert
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    public void ReviewService_Remove_Throws_WhenNotFound() {
        // Arrange
        given(reviewRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.remove(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Review: 1");
    }
}
