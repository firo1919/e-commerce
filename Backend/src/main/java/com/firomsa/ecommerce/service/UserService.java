package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.exception.EmailAlreadyExistsException;
import com.firomsa.ecommerce.exception.UserNameAlreadyExistsException;
import com.firomsa.ecommerce.exception.UserNotFoundException;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getUsers(){
        return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
    }

    public UserResponseDTO getUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if(userRepository.existsByUserName(userRequestDTO.getUserName())){
            throw new UserNameAlreadyExistsException(userRequestDTO.getUserName());
        }
        User user = userRepository.save(UserMapper.toModel(userRequestDTO));
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO, UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));

        if(userRepository.existsByEmailAndIdNot(userRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if(userRepository.existsByUserName(userRequestDTO.getUserName())){
            throw new UserNameAlreadyExistsException(userRequestDTO.getUserName());
        }

        user.setLastName(userRequestDTO.getLastName());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setUserName(userRequestDTO.getUserName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setActive(Boolean.parseBoolean(userRequestDTO.getIsActive()));
        user.setRole(Enum.valueOf(Role.class, userRequestDTO.getRole()));
        user.setUpdatedAt(LocalDateTime.now());

        return  UserMapper.toDTO(userRepository.save(user));
    }

    public void removeUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
        userRepository.delete(user);
    }

    public void softDeleteUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
        user.setActive(false);
        userRepository.save(user);
    }
}
