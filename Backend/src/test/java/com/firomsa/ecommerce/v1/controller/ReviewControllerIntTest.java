package com.firomsa.ecommerce.v1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.repository.ReviewRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ReviewControllerIntTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ReviewRepository reviewRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        private Role role;
        private User testUser;
        private Product testProduct;
        private Review testReview;

        @BeforeEach
        void setUp() {
                role = Role.builder().name("USER").id(1).build();
                testUser = User.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .firstName("Test")
                                .lastName("User")
                                .password("password123")
                                .role(role)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .active(true)
                                .build();

                testProduct = Product.builder()
                                .name("Test Product")
                                .description("Test Product Description")
                                .price(99.99)
                                .stock(10)
                                .active(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                testReview = Review.builder()
                                .rating(5)
                                .comment("Excellent product!")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ReviewController_GetAllReviews_ReturnsListOfReviews() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                testReview.setUser(savedUser);
                testReview.setProduct(savedProduct);
                List<Review> reviews = reviewRepository.saveAll(List.of(testReview));

                // Act and Assert
                mockMvc.perform(get("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()", CoreMatchers.is(reviews.size())))
                                .andExpect(jsonPath("$[0].rating", CoreMatchers.is(reviews.getFirst().getRating())))
                                .andExpect(jsonPath("$[0].comment", CoreMatchers.is(reviews.getFirst().getComment())))
                                .andExpect(jsonPath("$[0].userId",
                                                CoreMatchers.is(reviews.getFirst().getUser().getId().toString())))
                                .andExpect(jsonPath("$[0].productId",
                                                CoreMatchers.is(reviews.getFirst().getProduct().getId().toString())));
        }

        @Test
        void ReviewController_GetAllReviews_Returns401_WhenNotAuthenticated() throws Exception {
                // Act and Assert
                mockMvc.perform(get("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ReviewController_GetReview_ReturnsReview_WhenReviewExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                testReview.setUser(savedUser);
                testReview.setProduct(savedProduct);
                Review savedReview = reviewRepository.save(testReview);

                // Act and Assert
                mockMvc.perform(get("/api/v1/reviews/{id}", savedReview.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.rating", CoreMatchers.is(savedReview.getRating())))
                                .andExpect(jsonPath("$.comment", CoreMatchers.is(savedReview.getComment())))
                                .andExpect(jsonPath("$.userId",
                                                CoreMatchers.is(savedReview.getUser().getId().toString())))
                                .andExpect(jsonPath("$.productId",
                                                CoreMatchers.is(savedReview.getProduct().getId().toString())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ReviewController_GetReview_Returns404_WhenReviewNotFound() throws Exception {
                // Arrange
                int nonExistentId = 999;

                // Act and Assert
                mockMvc.perform(get("/api/v1/reviews/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ReviewController_DeleteReview_Returns204_WhenReviewExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                testReview.setUser(savedUser);
                testReview.setProduct(savedProduct);
                Review savedReview = reviewRepository.save(testReview);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/reviews/{id}", savedReview.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void ReviewController_DeleteReview_Returns404_WhenReviewNotFound() throws Exception {
                // Arrange
                int nonExistentId = 999;

                // Act and Assert
                mockMvc.perform(delete("/api/v1/reviews/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }
}
