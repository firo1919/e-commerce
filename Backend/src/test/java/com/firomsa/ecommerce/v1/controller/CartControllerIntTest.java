package com.firomsa.ecommerce.v1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.CartRepository;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class CartControllerIntTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        private Role role;
        private User testUser;
        private Product testProduct;

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
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void CartController_GetAllCarts_ReturnsListOfCarts() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(Collections.singletonList(category));
                Product savedProduct = productRepository.save(testProduct);

                Cart cart = Cart.builder()
                                .user(savedUser)
                                .product(savedProduct)
                                .quantity(2)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                List<Cart> carts = cartRepository.saveAll(List.of(cart));

                // Act and Assert
                mockMvc.perform(get("/api/v1/carts").contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()", CoreMatchers.is(carts.size())))
                                .andExpect(jsonPath("$[0].quantity", CoreMatchers.is(carts.getFirst().getQuantity())))
                                .andExpect(jsonPath("$[0].productId",
                                                CoreMatchers.is(carts.getFirst().getProduct().getId().toString())))
                                .andExpect(jsonPath("$[0].userId",
                                                CoreMatchers.is(carts.getFirst().getUser().getId().toString())));
        }

        @Test
        void CartController_GetAllCarts_Returns401_WhenNotAuthenticated() throws Exception {
                // Act and Assert
                mockMvc.perform(get("/api/v1/carts").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void CartController_GetCart_ReturnsCart_WhenCartExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(Collections.singletonList(category));
                Product savedProduct = productRepository.save(testProduct);

                Cart cart = Cart.builder()
                                .user(savedUser)
                                .product(savedProduct)
                                .quantity(2)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                Cart savedCart = cartRepository.save(cart);

                // Act and Assert
                mockMvc.perform(get("/api/v1/carts/{id}", savedCart.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.quantity", CoreMatchers.is(savedCart.getQuantity())))
                                .andExpect(jsonPath("$.productId",
                                                CoreMatchers.is(savedCart.getProduct().getId().toString())))
                                .andExpect(jsonPath("$.userId",
                                                CoreMatchers.is(savedCart.getUser().getId().toString())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void CartController_GetCart_Returns404_WhenCartNotFound() throws Exception {
                // Arrange
                int nonExistentId = 999;

                // Act and Assert
                mockMvc.perform(get("/api/v1/carts/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        void CartController_DeleteCart_Returns204_WhenCartExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                SecurityContextHolder.setContext(getContext(savedUser));
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(Collections.singletonList(category));
                Product savedProduct = productRepository.save(testProduct);

                Cart cart = Cart.builder()
                                .user(savedUser)
                                .product(savedProduct)
                                .quantity(2)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                Cart savedCart = cartRepository.save(cart);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/carts/{id}", savedCart.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        void CartController_DeleteCart_Returns404_WhenCartNotFound() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                SecurityContextHolder.setContext(getContext(savedUser));
                int nonExistentId = 999;

                // Act and Assert
                mockMvc.perform(delete("/api/v1/carts/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        private SecurityContext getContext(User savedUser) {
                // Create a mock authentication with the user as principal
                User mockPrincipal = User.builder()
                                .id(savedUser.getId())
                                .username("firo1")
                                .role(role)
                                .build();

                // Set up security context manually
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                                mockPrincipal, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
                return context;
        }
}
