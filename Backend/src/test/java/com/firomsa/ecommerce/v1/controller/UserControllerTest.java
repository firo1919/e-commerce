package com.firomsa.ecommerce.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
