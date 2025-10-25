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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.OrderStatus;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.OrderRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private AddressRepository addressRepository;

        private Role role;
        private User testUser;
        private Product testProduct;
        private Address testAddress;

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

                testAddress = Address.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .street("123 Main St")
                                .city("New York")
                                .state("NY")
                                .zipCode("10001")
                                .country("USA")
                                .phone("1234567890")
                                .active(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void OrderController_GetAllOrders_ReturnsListOfOrders() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                productRepository.save(testProduct);
                addressRepository.save(testAddress);

                Order order = Order.builder()
                                .user(savedUser)
                                .status(OrderStatus.PENDING)
                                .totalPrice(199.98)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                List<Order> orders = orderRepository.saveAll(List.of(order));

                // Act and Assert
                mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()", CoreMatchers.is(orders.size())))
                                .andExpect(jsonPath("$[0].status",
                                                CoreMatchers.is(orders.getFirst().getStatus().toString())))
                                .andExpect(jsonPath("$[0].totalPrice",
                                                CoreMatchers.is(orders.getFirst().getTotalPrice())))
                                .andExpect(jsonPath("$[0].userId",
                                                CoreMatchers.is(orders.getFirst().getUser().getId().toString())));
        }

        @Test
        void OrderController_GetAllOrders_Returns401_WhenNotAuthenticated() throws Exception {
                // Act and Assert
                mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void OrderController_GetOrder_ReturnsOrder_WhenOrderExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(Collections.singletonList(category));
                productRepository.save(testProduct);
                addressRepository.save(testAddress);

                Order order = Order.builder()
                                .user(savedUser)
                                .status(OrderStatus.PENDING)
                                .totalPrice(199.98)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                Order savedOrder = orderRepository.save(order);

                // Act and Assert
                mockMvc.perform(get("/api/v1/orders/{id}", savedOrder.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", CoreMatchers.is(savedOrder.getStatus().toString())))
                                .andExpect(jsonPath("$.totalPrice", CoreMatchers.is(savedOrder.getTotalPrice())))
                                .andExpect(jsonPath("$.userId",
                                                CoreMatchers.is(savedOrder.getUser().getId().toString())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void OrderController_GetOrder_Returns404_WhenOrderNotFound() throws Exception {
                // Arrange
                int nonExistentId = 999;

                // Act and Assert
                mockMvc.perform(get("/api/v1/orders/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void OrderController_DeleteOrder_Returns204_WhenOrderExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(testUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                testProduct.setCategories(Collections.singletonList(category));
                productRepository.save(testProduct);
                addressRepository.save(testAddress);

                Order order = Order.builder()
                                .user(savedUser)
                                .status(OrderStatus.PENDING)
                                .totalPrice(199.98)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                Order savedOrder = orderRepository.save(order);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/orders/{id}", savedOrder.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void OrderController_DeleteOrder_Returns404_WhenOrderNotFound() throws Exception {
                // Arrange
                int nonExistentId = 999;

                // Act and Assert
                mockMvc.perform(delete("/api/v1/orders/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }
}
