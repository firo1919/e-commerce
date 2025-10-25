package com.firomsa.ecommerce.v1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.AddressRepository;
import com.firomsa.ecommerce.repository.CartRepository;
import com.firomsa.ecommerce.repository.CategoryRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.repository.UserRepository;
import com.firomsa.ecommerce.v1.dto.AddressRequestDTO;
import com.firomsa.ecommerce.v1.dto.CartRequestDTO;
import com.firomsa.ecommerce.v1.dto.ReviewRequestDTO;
import com.firomsa.ecommerce.v1.dto.UserRequestDTO;
import com.firomsa.ecommerce.v1.service.PaymentService;
import com.yaphet.chapa.model.InitializeResponseData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class UserControllerIntTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private AddressRepository addressRepository;

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private PaymentService paymentService;

        private Role role;
        private User firstUser;
        private User secondUser;
        private Product testProduct;

        @BeforeEach
        void setUp() {
                role = Role.builder().name("USER").id(1).build();
                firstUser = User.builder().username("firo1").email("example@gmail.com")
                                .firstName("Firomsa").lastName("Assefa").password("123").role(role)
                                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).active(true)
                                .build();
                secondUser = User.builder().username("firo2").email("example2@gmail.com")
                                .firstName("Firomsa").lastName("Assefa").password("123").role(role)
                                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).active(true)
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
        void UserController_GetAllUsers_ReturnsListOfUsers() throws Exception {
                // Arrange
                List<User> users = userRepository.saveAll(List.of(firstUser, secondUser));

                // Act and Assert
                mockMvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()", CoreMatchers.is(users.size())))
                                .andExpect(jsonPath("$[0].username",
                                                CoreMatchers.is(users.getFirst().getUsername())))
                                .andExpect(jsonPath("$[0].email",
                                                CoreMatchers.is(users.getFirst().getEmail())))
                                .andExpect(jsonPath("$[0].firstName",
                                                CoreMatchers.is(users.getFirst().getFirstName())))
                                .andExpect(jsonPath("$[0].lastName",
                                                CoreMatchers.is(users.getFirst().getLastName())))
                                .andExpect(jsonPath("$[0].active",
                                                CoreMatchers.is(users.getFirst().isActive())))
                                .andExpect(jsonPath("$[0].role",
                                                CoreMatchers.is(users.getFirst().getRole().getName())));
        }

        @Test
        void UserController_GetAllUsers_ShouldReturn401_WhenNotAuthenticated() throws Exception {
                // Act and Assert
                mockMvc.perform(get("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_GetUser_ReturnsUser_WhenUserExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                // Act and Assert
                mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.username", CoreMatchers.is(savedUser.getUsername())))
                                .andExpect(jsonPath("$.email", CoreMatchers.is(savedUser.getEmail())))
                                .andExpect(jsonPath("$.firstName", CoreMatchers.is(savedUser.getFirstName())))
                                .andExpect(jsonPath("$.lastName", CoreMatchers.is(savedUser.getLastName())))
                                .andExpect(jsonPath("$.active", CoreMatchers.is(savedUser.isActive())))
                                .andExpect(jsonPath("$.role", CoreMatchers.is(savedUser.getRole().getName())));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_GetUser_Returns404_WhenUserNotFound() throws Exception {
                // Arrange
                UUID nonExistentId = UUID.randomUUID();

                // Act and Assert
                mockMvc.perform(get("/api/v1/users/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        void UserController_UpdateUser_ReturnsUpdatedUser_WhenValidData() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                SecurityContextHolder.setContext(getContext(savedUser));

                UserRequestDTO updateRequest = UserRequestDTO.builder()
                                .username("updatedUsername")
                                .email("updated@example.com")
                                .firstName("UpdatedFirst")
                                .lastName("UpdatedLast")
                                .password("newPassword")
                                .build();

                // Act and Assert
                mockMvc.perform(put("/api/v1/users/{id}", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$.username", CoreMatchers.is(updateRequest.getUsername())))
                                .andExpect(jsonPath("$.email", CoreMatchers.is(updateRequest.getEmail())))
                                .andExpect(jsonPath("$.firstName", CoreMatchers.is(updateRequest.getFirstName())))
                                .andExpect(jsonPath("$.lastName", CoreMatchers.is(updateRequest.getLastName())));
        }

        @Test
        void UserController_UpdateUser_Returns400_WhenInvalidData() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                UserRequestDTO invalidRequest = UserRequestDTO.builder()
                                .username("") // Invalid: empty username
                                .email("invalid-email") // Invalid: malformed email
                                .firstName("ValidFirst")
                                .lastName("ValidLast")
                                .password("validPassword")
                                .build();

                SecurityContextHolder.setContext(getContext(savedUser));

                // Act and Assert
                mockMvc.perform(put("/api/v1/users/{id}", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_DeleteUser_Returns204_WhenSoftDelete() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/users/{id}", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_DeleteUser_Returns204_WhenForceDelete() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                // Act and Assert
                mockMvc.perform(delete("/api/v1/users/{id}?force=true", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        // ========== CART TESTS ==========
        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_GetUserCarts_ReturnsListOfCarts_WhenUserExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                // Act and Assert
                mockMvc.perform(get("/api/v1/users/{id}/carts", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$", CoreMatchers.isA(List.class)));
        }

        @Test
        void UserController_AddItemToCart_ReturnsCart_WhenValidData() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                CartRequestDTO cartRequest = CartRequestDTO.builder()
                                .quantity(2)
                                .build();

                SecurityContextHolder.setContext(getContext(savedUser));

                // Act and Assert
                mockMvc.perform(post("/api/v1/users/{id}/carts?productId={productId}", savedUser.getId(),
                                savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartRequest)))
                                .andDo(print()).andExpect(status().isCreated())
                                .andExpect(jsonPath("$.quantity", CoreMatchers.is(cartRequest.getQuantity())));
        }

        // ========== ORDER TESTS ==========
        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_GetUserOrders_ReturnsListOfOrders_WhenUserExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                // Act and Assert
                mockMvc.perform(get("/api/v1/users/{id}/orders", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$", CoreMatchers.isA(List.class)));
        }

        @Test
        void UserController_CreateOrder_ReturnsOrderDetail_WhenUserExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                Address address = Address.builder().firstName("John").lastName("Doe").street("123 Main St")
                                .city("New York").state("NY").zipCode("10001").country("USA").phone("1234567890")
                                .active(true).user(savedUser).createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now()).build();
                addressRepository.save(address);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                cartRepository.save(Cart.builder().product(savedProduct).quantity(1).user(savedUser)
                                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
                given(paymentService.startTransaction(any(Order.class))).willReturn(new InitializeResponseData());
                SecurityContextHolder.setContext(getContext(savedUser));
                // Act and Assert
                mockMvc.perform(post("/api/v1/users/{id}/orders", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk());
        }

        // ========== ADDRESS TESTS ==========
        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_GetUserAddresses_ReturnsListOfAddresses_WhenUserExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                // Act and Assert
                mockMvc.perform(get("/api/v1/users/{id}/addresses", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$", CoreMatchers.isA(List.class)));
        }

        @Test
        void UserController_AddAddress_ReturnsAddress_WhenValidData() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                AddressRequestDTO addressRequest = AddressRequestDTO.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .street("123 Main St")
                                .city("New York")
                                .state("NY")
                                .zipCode("10001")
                                .country("USA")
                                .phone("1234567890")
                                .active(true)
                                .build();
                SecurityContextHolder.setContext(getContext(savedUser));

                // Act and Assert
                mockMvc.perform(post("/api/v1/users/{id}/addresses", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressRequest)))
                                .andDo(print()).andExpect(status().isCreated())
                                .andExpect(jsonPath("$.firstName", CoreMatchers.is(addressRequest.getFirstName())))
                                .andExpect(jsonPath("$.lastName", CoreMatchers.is(addressRequest.getLastName())))
                                .andExpect(jsonPath("$.street", CoreMatchers.is(addressRequest.getStreet())))
                                .andExpect(jsonPath("$.city", CoreMatchers.is(addressRequest.getCity())))
                                .andExpect(jsonPath("$.state", CoreMatchers.is(addressRequest.getState())))
                                .andExpect(jsonPath("$.zipCode", CoreMatchers.is(addressRequest.getZipCode())))
                                .andExpect(jsonPath("$.country", CoreMatchers.is(addressRequest.getCountry())))
                                .andExpect(jsonPath("$.phone", CoreMatchers.is(addressRequest.getPhone())));
        }

        @Test
        void UserController_AddAddress_Returns400_WhenInvalidData() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                AddressRequestDTO invalidRequest = AddressRequestDTO.builder()
                                .firstName("") // Invalid: empty first name
                                .lastName("Doe")
                                .street("123 Main St")
                                .city("New York")
                                .state("NY")
                                .zipCode("10001")
                                .country("USA")
                                .phone("1234567890")
                                .active(true)
                                .build();
                SecurityContextHolder.setContext(getContext(savedUser));

                // Act and Assert
                mockMvc.perform(post("/api/v1/users/{id}/addresses", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        // ========== REVIEW TESTS ==========
        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        void UserController_GetUserReviews_ReturnsListOfReviews_WhenUserExists() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);

                // Act and Assert
                mockMvc.perform(get("/api/v1/users/{id}/reviews", savedUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(jsonPath("$", CoreMatchers.isA(List.class)));
        }

        @Test
        void UserController_AddReview_ReturnsReview_WhenValidData() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                ReviewRequestDTO reviewRequest = ReviewRequestDTO.builder()
                                .rating(5)
                                .comment("Excellent product!")
                                .build();
                SecurityContextHolder.setContext(getContext(savedUser));

                // Act and Assert
                mockMvc.perform(post("/api/v1/users/{id}/reviews?productId={productId}", savedUser.getId(),
                                savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reviewRequest)))
                                .andDo(print()).andExpect(status().isCreated())
                                .andExpect(jsonPath("$.rating", CoreMatchers.is(reviewRequest.getRating())))
                                .andExpect(jsonPath("$.comment", CoreMatchers.is(reviewRequest.getComment())));
        }

        @Test
        void UserController_AddReview_Returns400_WhenInvalidRating() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                ReviewRequestDTO invalidRequest = ReviewRequestDTO.builder()
                                .rating(6) // Invalid: rating > 5
                                .comment("Invalid rating")
                                .build();
                SecurityContextHolder.setContext(getContext(savedUser));

                // Act and Assert
                mockMvc.perform(post("/api/v1/users/{id}/reviews?productId={productId}", savedUser.getId(),
                                savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void UserController_AddReview_Returns400_WhenEmptyComment() throws Exception {
                // Arrange
                User savedUser = userRepository.save(firstUser);
                var category = categoryRepository.findByName("Electronics").get();
                testProduct.setCategories(List.of(category));
                Product savedProduct = productRepository.save(testProduct);
                ReviewRequestDTO invalidRequest = ReviewRequestDTO.builder()
                                .rating(5)
                                .comment("") // Invalid: empty comment
                                .build();
                SecurityContextHolder.setContext(getContext(savedUser));
                // Act and Assert
                mockMvc.perform(post("/api/v1/users/{id}/reviews?productId={productId}", savedUser.getId(),
                                savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
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
