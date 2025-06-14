package com.firomsa.ecommerce.service;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void UserService_GetAllUsers_ReturnAllUsers() {
        User user1 = UserMapper.toModel(getUserRequestDTO("firo", "example@gmail.com", "Firomsa"));
        User user2 = UserMapper.toModel(getUserRequestDTO("kira", "example2@gmail.com", "Kirubel"));
        user1.setId(UUID.randomUUID());
        user1.setRole(Role.CUSTOMER);
        user2.setId(UUID.randomUUID());
        user2.setRole(Role.CUSTOMER);

        List<User> users = List.of(user1, user2);

        given(userRepository.findAll()).willReturn(users);

        assertThat(userService.getUsers()).isNotNull();
        assertThat(userService.getUsers().size()).isEqualTo(users.size());
    }

    @Test
    public void UserService_GetAUser_ReturnAUser() {
        User user = UserMapper.toModel(getUserRequestDTO("firo", "example@gmail.com", "Firomsa"));
        user.setId(UUID.randomUUID());
        user.setRole(Role.CUSTOMER);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserResponseDTO userResponseDTO = userService.getUser(user.getId());
        assertThat(userResponseDTO).isNotNull();
        assertThat(userResponseDTO.getId()).isEqualTo(user.getId().toString());
    }

    @Test
    public void UserService_CreateUser_ReturnSavedUserDTO(){
        UserRequestDTO userRequestDTO = getUserRequestDTO("firo", "example@gmail.com", "Firomsa");
        User user = UserMapper.toModel(userRequestDTO);
        user.setId(UUID.randomUUID());
        user.setRole(Role.CUSTOMER);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        // Controlling the output of the Mock userRepository save method in the createUser method being tested
        given(userRepository.save(Mockito.any(User.class))).willReturn(user);
        given(userRepository.existsByUserName(Mockito.anyString())).willReturn(false);
        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(false);

        UserResponseDTO responseDTO = userService.createUser(userRequestDTO);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(userResponseDTO.getId());
    }

    @Test
    public void UserService_CreateAdmin_ReturnSavedUserDTO(){
        UserRequestDTO userRequestDTO = getUserRequestDTO("firo", "example@gmail.com", "Firomsa");
        User user = UserMapper.toModel(userRequestDTO);
        user.setRole(Role.ADMIN);
        user.setId(UUID.randomUUID());
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        // Controlling the output of the Mock userRepository save method in the createUser method being tested
        given(userRepository.save(Mockito.any(User.class))).willReturn(user);
        given(userRepository.existsByUserName(Mockito.anyString())).willReturn(false);
        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(false);

        UserResponseDTO responseDTO = userService.createAdmin(userRequestDTO);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(userResponseDTO.getId());
    }

    @Test
    public void UserService_UpdateUser_ReturnUpdatedUserDTO(){
        UserRequestDTO userRequestDTO = getUserRequestDTO("firo", "example@gmail.com", "Firomsa");
        User user = UserMapper.toModel(userRequestDTO);
        user.setId(UUID.randomUUID());
        user.setRole(Role.CUSTOMER);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(Mockito.any(User.class))).willReturn(user);
        given(userRepository.existsByUserName(user.getUserName())).willReturn(false);
        given(userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId())).willReturn(false);

        UserResponseDTO responseDTO = userService.updateUser(userRequestDTO, user.getId());

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(userResponseDTO.getId());
    }

    @Test
    public void UserService_RemoveUser_CallsDeleteById(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .userName("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        userService.removeUser(user.getId());
        verify(userRepository).findById(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    public void UserService_SoftRemoveUser_CallsSave() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .userName("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        userService.softDeleteUser(user.getId());
        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
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
