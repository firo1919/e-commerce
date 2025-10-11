package com.firomsa.ecommerce.v1.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.security.JWTSecurityFilter;
import com.firomsa.ecommerce.v1.dto.UserRequestDTO;
import com.firomsa.ecommerce.v1.service.JWTAuthService;
import com.firomsa.ecommerce.v1.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTAuthService jwtAuthService;

    @MockitoBean
    private JWTSecurityFilter jwtSecurityFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Role role = Role.builder().name("USER").id(1).build();
    private final User firstUser = User.builder()
            .id(UUID.randomUUID())
            .username("firo")
            .email("example@gmail.com")
            .firstName("Firomsa")
            .lastName("Assefa")
            .password("123")
            .role(role)
            .active(true)
            .build();

    private final User secondUser = User.builder()
            .id(UUID.randomUUID())
            .username("firo")
            .email("example@gmail.com")
            .firstName("Firomsa")
            .lastName("Assefa")
            .password("123")
            .role(role)
            .active(true)
            .build();

    private final UserRequestDTO userRequestDTO = UserRequestDTO.builder()
            .username("firo")
            .email("example@gmail.com")
            .firstName("Firomsa")
            .lastName("Assefa")
            .password("123")
            .build();

    // @Test
    // @WithMockUser(username = "admin", roles = { "ADMIN" })
    // public void UserController_GetAllUsers_ReturnsListOfUsers() throws Exception
    // {
    // List<UserResponseDTO> responseDTO = List.of(UserMapper.toDTO(firstUser),
    // UserMapper.toDTO(secondUser));

    // given(userService.getAll()).willReturn(responseDTO);

    // ResultActions response = this.mockMvc.perform(get("/api/users")
    // .contentType(MediaType.APPLICATION_JSON));

    // response.andExpect(status().isOk())
    // .andExpect(jsonPath("$.size()", CoreMatchers.is(responseDTO.size())))
    // .andExpect(jsonPath("$[0].userName",
    // CoreMatchers.is(responseDTO.getFirst().getUsername())))
    // .andExpect(jsonPath("$[0].email",
    // CoreMatchers.is(responseDTO.getFirst().getEmail())))
    // .andExpect(jsonPath("$[0].firstName",
    // CoreMatchers.is(responseDTO.getFirst().getFirstName())));
    // }

    // @Test
    // public void UserController_GetUser_ReturnUser() throws Exception {
    // User user = UserMapper.toModel(getUserRequestDTO("kira",
    // "example2@gmail.com", "Kirubel"));
    // user.setId(UUID.randomUUID());
    // user.setRole(Role.builder().name("USER").id(1).build());
    // UserResponseDTO responseDTO = UserMapper.toDTO(user);

    // given(userService.get(user.getId())).willReturn(responseDTO);

    // ResultActions response = this.mockMvc.perform(get("/api/users/{id}",
    // user.getId())
    // .contentType(MediaType.APPLICATION_JSON));

    // response.andExpect(status().isOk())
    // .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())))
    // .andExpect(jsonPath("$.userName",
    // CoreMatchers.is(responseDTO.getUsername())))
    // .andExpect(jsonPath("$.email", CoreMatchers.is(responseDTO.getEmail())))
    // .andExpect(jsonPath("$.firstName",
    // CoreMatchers.is(responseDTO.getFirstName())));
    // }

    // @Test
    // public void UserController_AddUser_ReturnCreatedUser() throws Exception {
    // UserRequestDTO userRequestDTO = getUserRequestDTO("kira",
    // "example2@gmail.com", "Kirubel");
    // User user = UserMapper.toModel(userRequestDTO);
    // user.setId(UUID.randomUUID());
    // user.setRole(Role.builder().name("USER").id(1).build());
    // UserResponseDTO responseDTO = UserMapper.toDTO(user);

    // given(userService.create(Mockito.any(UserRequestDTO.class))).willReturn(responseDTO);

    // ResultActions response = this.mockMvc.perform(post("/api/users")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(userRequestDTO)));

    // response.andExpect(status().isCreated())
    // .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())))
    // .andExpect(jsonPath("$.userName",
    // CoreMatchers.is(responseDTO.getUsername())))
    // .andExpect(jsonPath("$.email", CoreMatchers.is(responseDTO.getEmail())))
    // .andExpect(jsonPath("$.firstName",
    // CoreMatchers.is(responseDTO.getFirstName())));
    // }

    // @Test
    // public void UserController_UpdateUser_ReturnUpdatedUser() throws Exception {
    // UserRequestDTO userRequestDTO = getUserRequestDTO("kira",
    // "example2@gmail.com", "Kirubel");
    // User user = UserMapper.toModel(userRequestDTO);
    // user.setId(UUID.randomUUID());
    // user.setRole(Role.builder().name("USER").id(1).build());
    // UserResponseDTO responseDTO = UserMapper.toDTO(user);

    // given(userService.update(Mockito.any(UserRequestDTO.class),
    // Mockito.any(UUID.class)))
    // .willReturn(responseDTO);

    // ResultActions response = this.mockMvc.perform(put("/api/users/{id}",
    // user.getId())
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(userRequestDTO)));

    // response.andExpect(status().isOk())
    // .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())))
    // .andExpect(jsonPath("$.userName",
    // CoreMatchers.is(responseDTO.getUsername())))
    // .andExpect(jsonPath("$.email", CoreMatchers.is(responseDTO.getEmail())))
    // .andExpect(jsonPath("$.firstName",
    // CoreMatchers.is(responseDTO.getFirstName())));
    // }

    // @Test
    // public void UserController_DeleteUser_ReturnNothing() throws Exception {
    // UUID id = UUID.randomUUID();
    // doNothing().when(userService).remove(Mockito.any(UUID.class));

    // ResultActions response = this.mockMvc.perform(delete("/api/users/{id}", id)
    // .contentType(MediaType.APPLICATION_JSON));

    // response.andExpect(status().isNoContent());
    // }

    // private static UserRequestDTO getUserRequestDTO(String userName, String
    // email, String firstName) {
    // return UserRequestDTO.builder()
    // .username(userName)
    // .email(email)
    // .firstName(firstName)
    // .lastName("Assefa")
    // .password("123")
    // .build();
    // }
}