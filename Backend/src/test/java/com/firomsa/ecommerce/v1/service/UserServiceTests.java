package com.firomsa.ecommerce.v1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.RoleRepository;
import com.firomsa.ecommerce.repository.UserRepository;
import com.firomsa.ecommerce.v1.dto.UserRequestDTO;
import com.firomsa.ecommerce.v1.dto.UserResponseDTO;
import com.firomsa.ecommerce.v1.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

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

    @InjectMocks
    private UserService userService;

    @Test
    public void UserService_GetAllUsers_ReturnAllUsers() {
        // Arrange
        List<User> users = List.of(firstUser, secondUser);
        given(userRepository.findAll()).willReturn(users);

        // Act
        List<UserResponseDTO> result = userService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void UserService_GetAUser_ReturnAUser() {

        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));

        UserResponseDTO responseDTO = userService.get(firstUser.getId());
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(firstUser.getId().toString());
        assertThat(responseDTO.getEmail()).isEqualTo(firstUser.getEmail());
        assertThat(responseDTO.getFirstName()).isEqualTo(firstUser.getFirstName());
        assertThat(responseDTO.getLastName()).isEqualTo(firstUser.getLastName());
        assertThat(responseDTO.getRole()).isEqualTo(firstUser.getRole().getName());
        assertThat(responseDTO.isActive()).isEqualTo(firstUser.isActive());
        assertThat(responseDTO.getUsername()).isEqualTo(firstUser.getUsername());
    }

    @Test
    public void UserService_CreateUser_ReturnSavedUserDTO() {
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        // Controlling the output of the Mock userRepository save method in the
        // createUser method being tested
        given(userRepository.save(Mockito.any(User.class))).willReturn(firstUser);
        given(userRepository.existsByUsername(Mockito.anyString())).willReturn(false);
        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(false);
        given(roleRepository.findByName("USER")).willReturn(Optional.of(role));

        UserResponseDTO responseDTO = userService.create(userRequestDTO);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(userResponseDTO.getId());
        assertThat(responseDTO.getEmail()).isEqualTo(userResponseDTO.getEmail());
        assertThat(responseDTO.getFirstName()).isEqualTo(userResponseDTO.getFirstName());
        assertThat(responseDTO.getLastName()).isEqualTo(userResponseDTO.getLastName());
        assertThat(responseDTO.getRole()).isEqualTo(userResponseDTO.getRole());
        assertThat(responseDTO.getUsername()).isEqualTo(userResponseDTO.getUsername());
        assertThat(responseDTO.isActive()).isEqualTo(userResponseDTO.isActive());
    }

    @Test
    public void UserService_CreateAdmin_ReturnSavedUserDTO() {
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        // Controlling the output of the Mock userRepository save method in the
        // createUser method being tested
        given(userRepository.save(Mockito.any(User.class))).willReturn(firstUser);
        given(userRepository.existsByUsername(Mockito.anyString())).willReturn(false);
        given(userRepository.existsByEmail(Mockito.anyString())).willReturn(false);
        given(roleRepository.findByName("ADMIN")).willReturn(Optional.of(Role.builder().name("ADMIN").id(2).build()));

        UserResponseDTO responseDTO = userService.createAdmin(userRequestDTO);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(userResponseDTO.getId());
        assertThat(responseDTO.getEmail()).isEqualTo(userResponseDTO.getEmail());
        assertThat(responseDTO.getFirstName()).isEqualTo(userResponseDTO.getFirstName());
        assertThat(responseDTO.getLastName()).isEqualTo(userResponseDTO.getLastName());
        assertThat(responseDTO.getRole()).isEqualTo(userResponseDTO.getRole());
        assertThat(responseDTO.getUsername()).isEqualTo(userResponseDTO.getUsername());
        assertThat(responseDTO.isActive()).isEqualTo(userResponseDTO.isActive());
    }

    @Test
    public void UserService_UpdateUser_ReturnUpdatedUserDTO() {
        UserResponseDTO userResponseDTO = UserMapper.toDTO(firstUser);

        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        given(userRepository.save(Mockito.any(User.class))).willReturn(firstUser);
        given(userRepository.existsByUsernameAndIdNot(firstUser.getUsername(), firstUser.getId())).willReturn(false);
        given(userRepository.existsByEmailAndIdNot(firstUser.getEmail(), firstUser.getId())).willReturn(false);

        UserResponseDTO responseDTO = userService.update(userRequestDTO, firstUser.getId());

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(userResponseDTO.getId());
        assertThat(responseDTO.getEmail()).isEqualTo(userResponseDTO.getEmail());
        assertThat(responseDTO.getFirstName()).isEqualTo(userResponseDTO.getFirstName());
        assertThat(responseDTO.getLastName()).isEqualTo(userResponseDTO.getLastName());
        assertThat(responseDTO.getRole()).isEqualTo(userResponseDTO.getRole());
        assertThat(responseDTO.getUsername()).isEqualTo(userResponseDTO.getUsername());
        assertThat(responseDTO.isActive()).isEqualTo(userResponseDTO.isActive());
    }

    @Test
    public void UserService_RemoveUser_CallsDeleteById() {
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        userService.remove(firstUser.getId());
        verify(userRepository).findById(firstUser.getId());
        verify(userRepository).delete(firstUser);
    }

    @Test
    public void UserService_SoftRemoveUser_CallsSave() {
        given(userRepository.findById(firstUser.getId())).willReturn(Optional.of(firstUser));
        userService.softDelete(firstUser.getId());
        verify(userRepository).findById(firstUser.getId());
        verify(userRepository).save(firstUser);
    }
}
