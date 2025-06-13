package com.firomsa.ecommerce.service;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void UserService_Save_ReturnSavedUserDTO(){
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUserName("firo");
        userRequestDTO.setEmail("example@gmail.com");
        userRequestDTO.setFirstName("Firomsa");
        userRequestDTO.setLastName("Assefa");
        userRequestDTO.setPassword("123");
        userRequestDTO.setRole(Role.CUSTOMER.toString());
        userRequestDTO.setIsActive(Boolean.toString(true));
        User savedUser = UserMapper.toModel(userRequestDTO);
        savedUser.setId(UUID.randomUUID());

        // Controlling the output of the Mock userRepository save method in the createUser method being tested
        given(userRepository.save(Mockito.any(User.class))).willReturn(savedUser);
        given(userRepository.existsByUserName(Mockito.anyString())).willReturn(false);
        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(false);

        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);

        Assertions.assertThat(userResponseDTO).isNotNull();
        Assertions.assertThat(userResponseDTO.getId()).isNotNull();
    }
}
