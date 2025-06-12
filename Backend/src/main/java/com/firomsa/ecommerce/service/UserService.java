package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.exception.EmailAlreadyExistsException;
import com.firomsa.ecommerce.exception.UserNotFoundException;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getUsers(){
        return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
    }

    public Optional<UserResponseDTO> getUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return Optional.of(UserMapper.toDTO(user.get()));
        };

        return Optional.empty();
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }
        return UserMapper.toDTO(userRepository.save(UserMapper.toModel(userRequestDTO)));
    }

    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO, UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new UserNotFoundException(id.toString());
        });

        if(userRepository.existsByEmailAndIdNot(userRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        user.setLastName(userRequestDTO.getLastName());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setUserName(userRequestDTO.getUserName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setUpdatedAt(LocalDateTime.now());

        return  UserMapper.toDTO(userRepository.save(user));
    }

    public void removeUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(() ->{
            throw new UserNotFoundException(id.toString());
        });
        userRepository.delete(user);
    }
}
