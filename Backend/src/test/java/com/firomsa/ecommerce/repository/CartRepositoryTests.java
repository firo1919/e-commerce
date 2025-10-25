package com.firomsa.ecommerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class CartRepositoryTests {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;
    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setup() {
        role = roleRepository.save(Role.builder().name("USER").build());

        testUser = User.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role) // role is now initialized
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testProduct = Product.builder()
                .name("Test Product")
                .description("A test product")
                .price(100.0)
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testCart = Cart.builder()
                .quantity(2)
                .user(testUser)
                .product(testProduct)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void CartRepository_Save_ReturnSavedCart() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Cart cart = testCart;
        cart.setUser(savedUser);
        cart.setProduct(savedProduct);

        // Act
        Cart savedCart = cartRepository.save(cart);

        // Assert
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getId()).isNotNull();
        assertThat(savedCart).usingRecursiveComparison().isEqualTo(cart);
        assertThat(savedCart.getCreatedAt()).isNotNull();
        assertThat(savedCart.getUpdatedAt()).isNotNull();
    }

    @Test
    public void CartRepository_FindById_ReturnCart() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Cart cart = testCart;
        cart.setUser(savedUser);
        cart.setProduct(savedProduct);
        Cart savedCart = cartRepository.save(cart);

        // Act
        Optional<Cart> foundCart = cartRepository.findById(savedCart.getId());

        // Assert
        assertThat(foundCart).isPresent();
        Cart retrievedCart = foundCart.get();
        assertThat(retrievedCart).isNotNull();
        assertThat(retrievedCart).usingRecursiveComparison().isEqualTo(savedCart);
    }

    @Test
    public void CartRepository_FindById_ReturnEmpty() {
        // Arrange
        Integer nonExistentId = 999;

        // Act
        Optional<Cart> foundCart = cartRepository.findById(nonExistentId);

        // Assert
        assertThat(foundCart).isEmpty();
    }

    @Test
    public void CartRepository_FindByUserAndProduct_ReturnCart() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Cart cart = testCart;
        cart.setUser(savedUser);
        cart.setProduct(savedProduct);
        cartRepository.save(cart);

        // Act
        Optional<Cart> foundCart = cartRepository.findByUserAndProduct(savedUser, savedProduct);

        // Assert
        assertThat(foundCart).isPresent();
        Cart retrievedCart = foundCart.get();
        assertThat(retrievedCart).isNotNull();
        assertThat(retrievedCart).usingRecursiveComparison().isEqualTo(cart);
        assertThat(retrievedCart.getQuantity()).isEqualTo(2);
    }

    @Test
    public void CartRepository_FindByUserAndProduct_ReturnEmpty() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);

        // Act
        Optional<Cart> foundCart = cartRepository.findByUserAndProduct(savedUser, savedProduct);

        // Assert
        assertThat(foundCart).isEmpty();
    }

    @Test
    public void CartRepository_FindAllByUser_ReturnCarts() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Cart cart = testCart;
        cart.setUser(savedUser);
        cart.setProduct(savedProduct);
        cartRepository.save(cart);

        // Act
        List<Cart> foundCart = cartRepository.findAllByUser(savedUser);

        // Assert
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.get(0)).usingRecursiveComparison().isEqualTo(cart);
        assertThat(foundCart.size()).isEqualTo(1);
    }

    @Test
    public void CartRepository_FindAllByUser_ReturnEmpty() {
        // Arrange
        User savedUser = userRepository.save(testUser);

        // Act
        List<Cart> foundCart = cartRepository.findAllByUser(savedUser);

        // Assert
        assertThat(foundCart).isEmpty();
    }

    @Test
    public void CartRepository_DeleteAllByUser_DeleteAllUserCarts() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);

        Cart cart1 = testCart;
        cart1.setUser(savedUser);
        cart1.setProduct(savedProduct);

        Cart cart2 = Cart.builder()
                .quantity(1)
                .user(savedUser)
                .product(savedProduct)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        cartRepository.save(cart1);
        cartRepository.save(cart2);

        // Act
        cartRepository.deleteAllByUser(savedUser);

        // Assert
        List<Cart> remainingCarts = cartRepository.findAll();
        assertThat(remainingCarts).isEmpty();
    }

    @Test
    public void CartRepository_DeleteById_DeleteCart() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Cart cart = testCart;
        cart.setUser(savedUser);
        cart.setProduct(savedProduct);
        Cart savedCart = cartRepository.save(cart);

        // Act
        cartRepository.deleteById(savedCart.getId());

        // Assert
        assertThat(cartRepository.existsById(savedCart.getId())).isFalse();
    }

    @Test
    public void CartRepository_Delete_DeleteCart() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Product savedProduct = productRepository.save(testProduct);
        Cart cart = testCart;
        cart.setUser(savedUser);
        cart.setProduct(savedProduct);
        Cart savedCart = cartRepository.save(cart);

        // Act
        cartRepository.delete(savedCart);

        // Assert
        assertThat(cartRepository.existsById(savedCart.getId())).isFalse();
    }
}
