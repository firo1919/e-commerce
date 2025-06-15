package com.firomsa.ecommerce.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void UserController_GetAllUsers_ReturnsListOfUsers() throws Exception {
        User user1 = UserMapper.toModel(getUserRequestDTO("firo", "example@gmail.com", "Firomsa"));
        User user2 = UserMapper.toModel(getUserRequestDTO("kira", "example2@gmail.com", "Kirubel"));
        user1.setId(UUID.randomUUID());
        user1.setRole(Role.CUSTOMER);
        user2.setId(UUID.randomUUID());
        user2.setRole(Role.CUSTOMER);
        List<UserResponseDTO> responseDTO = List.of(UserMapper.toDTO(user2), UserMapper.toDTO(user1));

        given(userService.getUsers()).willReturn(responseDTO);

        ResultActions response = this.mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(responseDTO.size())))
                .andExpect(jsonPath("$[0].userName", CoreMatchers.is(responseDTO.get(0).getUserName())))
                .andExpect(jsonPath("$[0].email", CoreMatchers.is(responseDTO.get(0).getEmail())))
                .andExpect(jsonPath("$[0].firstName", CoreMatchers.is(responseDTO.get(0).getFirstName())));
    }

    @Test
    public void UserController_GetUser_ReturnUser() throws Exception {
        User user = UserMapper.toModel(getUserRequestDTO("kira", "example2@gmail.com", "Kirubel"));
        user.setId(UUID.randomUUID());
        user.setRole(Role.CUSTOMER);
        UserResponseDTO responseDTO = UserMapper.toDTO(user);

        given(userService.getUser(user.getId())).willReturn(responseDTO);

        ResultActions response = this.mockMvc.perform(get("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())))
                .andExpect(jsonPath("$.userName", CoreMatchers.is(responseDTO.getUserName())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(responseDTO.getEmail())))
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(responseDTO.getFirstName())));
    }

    @Test
    public void UserController_AddUser_ReturnCreatedUser() throws Exception {
        UserRequestDTO userRequestDTO = getUserRequestDTO("kira", "example2@gmail.com", "Kirubel");
        User user = UserMapper.toModel(userRequestDTO);
        user.setId(UUID.randomUUID());
        user.setRole(Role.CUSTOMER);
        UserResponseDTO responseDTO = UserMapper.toDTO(user);

        given(userService.createUser(Mockito.any(UserRequestDTO.class))).willReturn(responseDTO);

        ResultActions response = this.mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())))
                .andExpect(jsonPath("$.userName", CoreMatchers.is(responseDTO.getUserName())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(responseDTO.getEmail())))
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(responseDTO.getFirstName())));
    }

    @Test
    public void UserController_UpdateUser_ReturnUpdatedUser() throws Exception {
        UserRequestDTO userRequestDTO = getUserRequestDTO("kira", "example2@gmail.com", "Kirubel");
        User user = UserMapper.toModel(userRequestDTO);
        user.setId(UUID.randomUUID());
        user.setRole(Role.CUSTOMER);
        UserResponseDTO responseDTO = UserMapper.toDTO(user);

        given(userService.updateUser(Mockito.any(UserRequestDTO.class), Mockito.any(UUID.class)))
                .willReturn(responseDTO);

        ResultActions response = this.mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(responseDTO.getId())))
                .andExpect(jsonPath("$.userName", CoreMatchers.is(responseDTO.getUserName())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(responseDTO.getEmail())))
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(responseDTO.getFirstName())));
    }

    @Test
    public void UserController_DeleteUser_ReturnNothing() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).removeUser(Mockito.any(UUID.class));

        ResultActions response = this.mockMvc.perform(delete("/api/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    private static UserRequestDTO getUserRequestDTO(String userName, String email, String firstName) {
        return UserRequestDTO.builder()
                .userName(userName)
                .email(email)
                .firstName(firstName)
                .lastName("Assefa")
                .password("123")
                .build();
    }
}