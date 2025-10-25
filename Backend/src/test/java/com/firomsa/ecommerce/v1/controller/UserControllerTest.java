package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.AddressRequestDTO;
import com.firomsa.ecommerce.v1.dto.AddressResponseDTO;
import com.firomsa.ecommerce.v1.dto.CartRequestDTO;
import com.firomsa.ecommerce.v1.dto.CartResponseDTO;
import com.firomsa.ecommerce.v1.dto.OrderDetailDTO;
import com.firomsa.ecommerce.v1.dto.OrderResponseDTO;
import com.firomsa.ecommerce.v1.dto.ReviewRequestDTO;
import com.firomsa.ecommerce.v1.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.v1.dto.UserRequestDTO;
import com.firomsa.ecommerce.v1.dto.UserResponseDTO;
import com.firomsa.ecommerce.v1.mapper.UserMapper;
import com.firomsa.ecommerce.v1.service.JWTAuthService;
import com.firomsa.ecommerce.v1.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @Autowired
    private MockMvc mockMvc;

    private Role role;

    private User firstUser;

    private User secondUser;

    private UserRequestDTO userRequestDTO;
    private CartResponseDTO cartResponseDTO;
    private CartRequestDTO cartRequestDTO;
    private OrderResponseDTO orderResponseDTO;
    private OrderDetailDTO orderDetailDTO;
    private AddressResponseDTO addressResponseDTO;
    private AddressRequestDTO addressRequestDTO;
    private ReviewResponseDTO reviewResponseDTO;
    private ReviewRequestDTO reviewRequestDTO;

    @BeforeEach
    void setup() {
        role = Role.builder().name("USER").id(1).build();

        firstUser = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .build();

        secondUser = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(role)
                .active(true)
                .build();

        userRequestDTO = UserRequestDTO.builder()
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .build();

        cartResponseDTO = CartResponseDTO.builder().id(1).quantity(2).build();
        cartRequestDTO = CartRequestDTO.builder().quantity(2).build();
        orderResponseDTO = OrderResponseDTO.builder().id(1).totalPrice(100.0).build();
        orderDetailDTO = OrderDetailDTO.builder().order(orderResponseDTO).address(null).build();
        addressResponseDTO = AddressResponseDTO.builder().id(1).street("123 Main St").build();
        addressRequestDTO = AddressRequestDTO.builder().firstName("F").lastName("A").street("123 Main St").city("AA")
                .state("AA").zipCode("1000").country("ET").phone("+2519").active(true).build();
        reviewResponseDTO = ReviewResponseDTO.builder().id(1).rating(5).comment("Great").build();
        reviewRequestDTO = ReviewRequestDTO.builder().rating(5).comment("Great").build();
    }

    @Test
    public void UserController_GetAllUsers_ReturnsListOfUsers()
            throws Exception {
        // Arrange

        List<UserResponseDTO> responseDTO = List.of(
                UserMapper.toDTO(firstUser),
                UserMapper.toDTO(secondUser));

        given(userService.getAll()).willReturn(responseDTO);

        // Act and Assert
        mockMvc
                .perform(
                        get("/api/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.size()", CoreMatchers.is(responseDTO.size())))
                .andExpect(
                        jsonPath(
                                "$[0].username",
                                CoreMatchers.is(responseDTO.getFirst().getUsername())))
                .andExpect(
                        jsonPath(
                                "$[0].email",
                                CoreMatchers.is(responseDTO.getFirst().getEmail())))
                .andExpect(
                        jsonPath(
                                "$[0].firstName",
                                CoreMatchers.is(responseDTO.getFirst().getFirstName())))
                .andExpect(
                        jsonPath(
                                "$[0].lastName",
                                CoreMatchers.is(responseDTO.getFirst().getLastName())))
                .andExpect(
                        jsonPath(
                                "$[0].active",
                                CoreMatchers.is(responseDTO.getFirst().isActive())))
                .andExpect(
                        jsonPath(
                                "$[0].role",
                                CoreMatchers.is(responseDTO.getFirst().getRole())));

        verify(userService, times(1)).getAll();
    }

    @Test
    public void UserController_GetUser_ReturnsUser() throws Exception {
        // Arrange
        UserResponseDTO responseDTO = UserMapper.toDTO(firstUser);
        given(userService.get(firstUser.getId())).willReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", firstUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(responseDTO.getEmail())));

        verify(userService, times(1)).get(firstUser.getId());
    }

    @Test
    public void UserController_UpdateUser_ReturnsUpdated() throws Exception {
        // Arrange
        UserResponseDTO responseDTO = UserMapper.toDTO(firstUser);
        given(userService.update(org.mockito.ArgumentMatchers.any(UserRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(firstUser.getId()))).willReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", firstUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(userRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())));

        verify(userService, times(1)).update(org.mockito.ArgumentMatchers.any(UserRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(firstUser.getId()));
    }

    @Test
    public void UserController_DeleteUser_ForceTrue_CallsHardDelete() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", firstUser.getId())
                .param("force", "true"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).remove(firstUser.getId());
    }

    @Test
    public void UserController_DeleteUser_ForceFalse_CallsSoftDelete() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", firstUser.getId())
                .param("force", "false"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).softDelete(firstUser.getId());
    }

    @Test
    public void UserController_DeleteUser_NoParam_CallsSoftDelete() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", firstUser.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).softDelete(firstUser.getId());
    }

    @Test
    public void UserController_GetUserCarts_ReturnsCarts() throws Exception {
        // Arrange
        given(userService.getCarts(firstUser.getId())).willReturn(List.of(cartResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/carts", firstUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));

        verify(userService, times(1)).getCarts(firstUser.getId());
    }

    @Test
    public void UserController_AddItemToUserCart_CreatesAndReturnsLocation() throws Exception {
        // Arrange
        given(userService.addItemToCart(org.mockito.ArgumentMatchers.eq(firstUser.getId()),
                org.mockito.ArgumentMatchers.any(CartRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(secondUser.getId())))
                .willReturn(cartResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/{id}/carts", firstUser.getId())
                .param("productId", secondUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(cartRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", CoreMatchers.is(cartResponseDTO.getId())))
                .andExpect(result -> {
                    String location = result.getResponse().getHeader("Location");
                    org.assertj.core.api.Assertions.assertThat(location).contains("/api/carts/");
                });

        verify(userService, times(1)).addItemToCart(org.mockito.ArgumentMatchers.eq(firstUser.getId()),
                org.mockito.ArgumentMatchers.any(CartRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(secondUser.getId()));
    }

    @Test
    public void UserController_GetUserOrders_ReturnsOrders() throws Exception {
        // Arrange
        given(userService.getOrders(firstUser.getId())).willReturn(List.of(orderResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/orders", firstUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));

        verify(userService, times(1)).getOrders(firstUser.getId());
    }

    @Test
    public void UserController_CreateOrder_ReturnsOrderDetail() throws Exception {
        // Arrange
        given(userService.addOrder(firstUser.getId())).willReturn(orderDetailDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/{id}/orders", firstUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.id", CoreMatchers.is(orderDetailDTO.getOrder().getId())));

        verify(userService, times(1)).addOrder(firstUser.getId());
    }

    @Test
    public void UserController_GetUserAddresses_ReturnsAddresses() throws Exception {
        // Arrange
        given(userService.getAddresses(firstUser.getId())).willReturn(List.of(addressResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/addresses", firstUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));

        verify(userService, times(1)).getAddresses(firstUser.getId());
    }

    @Test
    public void UserController_AddAddress_CreatesWithLocation() throws Exception {
        // Arrange
        given(userService.addAddressToAddresses(org.mockito.ArgumentMatchers.eq(firstUser.getId()),
                org.mockito.ArgumentMatchers.any(AddressRequestDTO.class))).willReturn(addressResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/{id}/addresses", firstUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(addressRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", CoreMatchers.is(addressResponseDTO.getId())))
                .andExpect(
                        result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getHeader("Location"))
                                .contains("/api/addresses/"));

        verify(userService, times(1)).addAddressToAddresses(org.mockito.ArgumentMatchers.eq(firstUser.getId()),
                org.mockito.ArgumentMatchers.any(AddressRequestDTO.class));
    }

    @Test
    public void UserController_UpdateAddress_ReturnsUpdated() throws Exception {
        // Arrange
        given(userService.updateAddress(org.mockito.ArgumentMatchers.any(AddressRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(firstUser.getId())))
                .willReturn(addressResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{userId}/addresses/{addressId}", firstUser.getId(), 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(addressRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(addressResponseDTO.getId())));

        verify(userService, times(1)).updateAddress(org.mockito.ArgumentMatchers.any(AddressRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(firstUser.getId()));
    }

    @Test
    public void UserController_DeleteAddress_NoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{userId}/addresses/{addressId}", firstUser.getId(), 1))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).removeAddress(1, firstUser.getId());
    }

    @Test
    public void UserController_GetUserReviews_ReturnsReviews() throws Exception {
        // Arrange
        given(userService.getReviews(firstUser.getId())).willReturn(List.of(reviewResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/reviews", firstUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)));

        verify(userService, times(1)).getReviews(firstUser.getId());
    }

    @Test
    public void UserController_AddReview_CreatesWithLocation() throws Exception {
        // Arrange
        given(userService.addReviewToReviews(org.mockito.ArgumentMatchers.eq(firstUser.getId()),
                org.mockito.ArgumentMatchers.any(ReviewRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(secondUser.getId())))
                .willReturn(reviewResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/{id}/reviews", firstUser.getId())
                .param("productId", secondUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reviewRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", CoreMatchers.is(reviewResponseDTO.getId())))
                .andExpect(
                        result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getHeader("Location"))
                                .contains("/api/reviews/"));

        verify(userService, times(1)).addReviewToReviews(org.mockito.ArgumentMatchers.eq(firstUser.getId()),
                org.mockito.ArgumentMatchers.any(ReviewRequestDTO.class),
                org.mockito.ArgumentMatchers.eq(secondUser.getId()));
    }
}
