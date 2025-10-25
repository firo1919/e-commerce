package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Category;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Review;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Role role;
    private User testUser;
    private Category testCategory;
    private Product testProduct;
    private Review testReview1;
    private Review testReview2;

    @BeforeEach
    void setup() {
        role = roleRepository.save(Role.builder().name("USER").build());
        testCategory = categoryRepository.save(Category.builder().name("Electronics").build());
        testProduct = productRepository.save(Product.builder()
                .name("Test Product")
                .description("A test product")
                .price(100.0)
                .stock(10)
                .categories(new ArrayList<Category>(List.of(testCategory)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        testReview1 = Review.builder()
                .rating(5)
                .comment("Great product!")
                .user(testUser)
                .product(testProduct)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testReview2 = Review.builder()
                .rating(4)
                .comment("Good product")
                .user(testUser)
                .product(testProduct)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(User.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build());
    }

    @Test
    public void ReviewRepository_Save_ReturnSavedReview() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Review review = testReview1;
        review.setUser(savedUser);
        review.setProduct(savedProduct);

        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isNotNull();
        assertThat(savedReview).usingRecursiveComparison().isEqualTo(review);
        assertThat(savedReview.getCreatedAt()).isNotNull();
        assertThat(savedReview.getUpdatedAt()).isNotNull();
    }

    @Test
    public void ReviewRepository_FindAll_ReturnMoreThanOneReview() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);

        Review review1 = testReview1;
        Review review2 = testReview2;
        review1.setUser(savedUser);
        review1.setProduct(savedProduct);
        review2.setUser(savedUser);
        review2.setProduct(savedProduct);

        // Act
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        List<Review> savedReviews = reviewRepository.findAll();

        // Assert
        assertThat(savedReviews).isNotNull();
        assertThat(savedReviews.size()).isEqualTo(2);
        assertThat(savedReviews).extracting(Review::getRating).containsExactlyInAnyOrder(5, 4);
        assertThat(savedReviews).extracting(Review::getComment).containsExactlyInAnyOrder("Great product!",
                "Good product");
    }

    @Test
    public void ReviewRepository_FindById_ReturnReview() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Review review = testReview1;
        review.setUser(savedUser);
        review.setProduct(savedProduct);
        Review savedReview = reviewRepository.save(review);

        // Act
        Optional<Review> foundReview = reviewRepository.findById(savedReview.getId());

        // Assert
        assertThat(foundReview).isPresent();
        Review retrievedReview = foundReview.get();
        assertThat(retrievedReview).isNotNull();
        assertThat(retrievedReview).usingRecursiveComparison().isEqualTo(savedReview);
    }

    @Test
    public void ReviewRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<Review> foundReview = reviewRepository.findById(nonExistentId);

        // Assert
        assertThat(foundReview).isEmpty();
    }

    @Test
    public void ReviewRepository_DeleteById_DeleteReview() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Review review = testReview1;
        review.setUser(savedUser);
        review.setProduct(savedProduct);
        Review savedReview = reviewRepository.save(review);

        // Act
        reviewRepository.deleteById(savedReview.getId());

        // Assert
        assertThat(reviewRepository.existsById(savedReview.getId())).isFalse();
    }

    @Test
    public void ReviewRepository_Delete_DeleteReview() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Review review = testReview1;
        review.setUser(savedUser);
        review.setProduct(savedProduct);
        Review savedReview = reviewRepository.save(review);

        // Act
        reviewRepository.delete(savedReview);

        // Assert
        assertThat(reviewRepository.existsById(savedReview.getId())).isFalse();
    }
}
