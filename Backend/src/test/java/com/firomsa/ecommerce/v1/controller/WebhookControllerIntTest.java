package com.firomsa.ecommerce.v1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.config.PaymentConfig;
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
import com.firomsa.ecommerce.v1.dto.ChapaResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class WebhookControllerIntTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentConfig paymentConfig;

    private Role role;
    private User testUser;
    private Product testProduct;
    private Address testAddress;
    private Order testOrder;

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

        testOrder = Order.builder()
                .status(OrderStatus.PENDING)
                .totalPrice(199.98)
                .txRef("test-tx-ref-123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void WebhookController_ChapaWebhook_Returns204_WhenValidSignature() throws Exception {
        // Arrange
        User savedUser = userRepository.save(testUser);
        var category = categoryRepository.findByName("Electronics").get();
        testProduct.setCategories(List.of(category));
        productRepository.save(testProduct);
        addressRepository.save(testAddress);
        testOrder.setUser(savedUser);
        Order savedOrder = orderRepository.save(testOrder);

        ChapaResponse chapaResponse = ChapaResponse.builder()
                .status("success")
                .tx_ref(savedOrder.getTxRef())
                .build();
        String body = objectMapper.writeValueAsString(chapaResponse);

        String secret = paymentConfig.getEncription();
        javax.crypto.Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(body.getBytes(StandardCharsets.UTF_8));
        String hex = java.util.HexFormat.of().formatHex(hash);

        mockMvc.perform(post("/api/v1/webhook/payment").header("x-chapa-signature", hex).content(body))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void WebhookController_ChapaWebhook_Returns401_WhenInvalidSignature() throws Exception {
        // Arrange
        User savedUser = userRepository.save(testUser);
        var category = categoryRepository.findByName("Electronics").get();
        testProduct.setCategories(List.of(category));
        productRepository.save(testProduct);
        addressRepository.save(testAddress);
        testOrder.setUser(savedUser);
        Order savedOrder = orderRepository.save(testOrder);

        ChapaResponse chapaResponse = ChapaResponse.builder()
                .status("success")
                .tx_ref(savedOrder.getTxRef())
                .build();

        String requestBody = objectMapper.writeValueAsString(chapaResponse);
        String invalidSignature = "invalid-signature";

        // Act and Assert
        mockMvc.perform(post("/api/v1/webhook/payment")
                .header("x-chapa-signature", invalidSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void WebhookController_ChapaWebhook_Returns401_WhenNoSignature() throws Exception {
        // Arrange
        User savedUser = userRepository.save(testUser);
        var category = categoryRepository.findByName("Electronics").get();
        testProduct.setCategories(List.of(category));
        productRepository.save(testProduct);
        addressRepository.save(testAddress);
        testOrder.setUser(savedUser);
        Order savedOrder = orderRepository.save(testOrder);

        ChapaResponse chapaResponse = ChapaResponse.builder()
                .status("success")
                .tx_ref(savedOrder.getTxRef())
                .build();

        String requestBody = objectMapper.writeValueAsString(chapaResponse);

        // Act and Assert
        mockMvc.perform(post("/api/v1/webhook/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
