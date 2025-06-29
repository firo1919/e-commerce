package com.firomsa.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
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

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.RoleRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private final Role role = Role.builder().name("USER").id(1).build();

    @InjectMocks
    private UserService userService;

    @Test
    public void UserService_GetAllUsers_ReturnAllUsers() {
        User user1 = UserMapper.toModel(getUserRequestDTO("firo", "example@gmail.com", "Firomsa"));
        User user2 = UserMapper.toModel(getUserRequestDTO("kira", "example2@gmail.com", "Kirubel"));
        user1.setId(UUID.randomUUID());
        user1.setRole(role);
        user2.setId(UUID.randomUUID());
        user2.setRole(role);

        List<User> users = List.of(user1, user2);

        given(userRepository.findAll()).willReturn(users);

        assertThat(userService.getAll()).isNotNull();
        assertThat(userService.getAll().size()).isEqualTo(users.size());
    }

    @Test
    public void UserService_GetAUser_ReturnAUser() {
        User user = UserMapper.toModel(getUserRequestDTO("firo", "example@gmail.com", "Firomsa"));
        user.setId(UUID.randomUUID());
        user.setRole(role);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserResponseDTO responseDTO = userService.get(user.getId());
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(user.getId().toString());
        assertThat(responseDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(responseDTO.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(responseDTO.getLastName()).isEqualTo(user.getLastName());
        assertThat(responseDTO.getRole()).isEqualTo(user.getRole().getName());
        assertThat(responseDTO.isActive()).isEqualTo(Boolean.toString(user.isActive()));
        assertThat(responseDTO.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void UserService_CreateUser_ReturnSavedUserDTO(){
        UserRequestDTO userRequestDTO = getUserRequestDTO("firo", "example@gmail.com", "Firomsa");
        User user = UserMapper.toModel(userRequestDTO);
        user.setId(UUID.randomUUID());
        user.setRole(role);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        // Controlling the output of the Mock userRepository save method in the createUser method being tested
        given(userRepository.save(Mockito.any(User.class))).willReturn(user);
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
    public void UserService_CreateAdmin_ReturnSavedUserDTO(){
        UserRequestDTO userRequestDTO = getUserRequestDTO("firo", "example@gmail.com", "Firomsa");
        User user = UserMapper.toModel(userRequestDTO);
        user.setRole(Role.builder().name("ADMIN").id(2).build());
        user.setId(UUID.randomUUID());
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        // Controlling the output of the Mock userRepository save method in the createUser method being tested
        given(userRepository.save(Mockito.any(User.class))).willReturn(user);
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
    public void UserService_UpdateUser_ReturnUpdatedUserDTO(){
        UserRequestDTO userRequestDTO = getUserRequestDTO("firo", "example@gmail.com", "Firomsa");
        User user = UserMapper.toModel(userRequestDTO);
        user.setId(UUID.randomUUID());
        user.setRole(role);
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(Mockito.any(User.class))).willReturn(user);
        given(userRepository.existsByUsername(user.getUsername())).willReturn(false);
        given(userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId())).willReturn(false);

        UserResponseDTO responseDTO = userService.update(userRequestDTO, user.getId());

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
    public void UserService_RemoveUser_CallsDeleteById(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .role(Role.builder().name("USER").id(1).build())
                .active(true)
                .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        userService.remove(user.getId());
        verify(userRepository).findById(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    public void UserService_SoftRemoveUser_CallsSave() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("firo")
                .email("example@gmail.com")
                .firstName("Firomsa")
                .lastName("Assefa")
                .password("123")
                .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        userService.softDelete(user.getId());
        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
    }

    private static UserRequestDTO getUserRequestDTO(String userName, String email, String firstName) {
        return UserRequestDTO.builder()
                .username(userName)
                .email(email)
                .firstName(firstName)
                .lastName("Assefa")
                .password("123")
                .build();
    }
}
