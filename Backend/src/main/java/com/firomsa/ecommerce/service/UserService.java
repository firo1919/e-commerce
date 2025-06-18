package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.AddressResponseDTO;
import com.firomsa.ecommerce.dto.CartResponseDTO;
import com.firomsa.ecommerce.dto.OrderResponseDTO;
import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.exception.EmailAlreadyExistsException;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.exception.UserNameAlreadyExistsException;
import com.firomsa.ecommerce.mapper.AddressMapper;
import com.firomsa.ecommerce.mapper.CartMapper;
import com.firomsa.ecommerce.mapper.OrderMapper;
import com.firomsa.ecommerce.mapper.ReviewMapper;
import com.firomsa.ecommerce.mapper.UserMapper;
import com.firomsa.ecommerce.model.Role;
import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.RoleRepository;
import com.firomsa.ecommerce.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserResponseDTO> getAll(){
        return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
    }

    public UserResponseDTO get(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO create(UserRequestDTO userRequestDTO){
        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if(userRepository.existsByUserName(userRequestDTO.getUserName())){
            throw new UserNameAlreadyExistsException(userRequestDTO.getUserName());
        }
        Role role = roleRepository.findByName("USER").orElseThrow(() -> new ResourceNotFoundException("Role User not found"));
        User model = UserMapper.toModel(userRequestDTO);
        model.setRole(role);
        model.setActive(true);
        User user = userRepository.save(model);
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO createAdmin(UserRequestDTO userRequestDTO){
        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException(userRequestDTO.getEmail());
        }

        if(userRepository.existsByUserName(userRequestDTO.getUserName())){
            throw new UserNameAlreadyExistsException(userRequestDTO.getUserName());
        }
        Role role = roleRepository.findByName("ADMIN").orElseThrow(() -> new ResourceNotFoundException("Role Admin not found"));
        User model = UserMapper.toModel(userRequestDTO);
        model.setRole(role);
        model.setActive(true);
        User user = userRepository.save(model);
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO update(UserRequestDTO userRequestDTO, UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));

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
        user.setUpdatedAt(LocalDateTime.now());

        return  UserMapper.toDTO(userRepository.save(user));
    }

    public void remove(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        userRepository.delete(user);
    }

    public void softDelete(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        user.setActive(false);
        userRepository.save(user);
    }

    public List<CartResponseDTO> getCarts(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        return user.getCarts().stream().map(CartMapper::toDTO).toList();
    }

    public List<OrderResponseDTO> getOrders(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        return user.getOrders().stream().map(OrderMapper::toDTO).toList();
    }

    public List<AddressResponseDTO> getAddresses(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        return user.getAddresses().stream().map(AddressMapper::toDTO).toList();
    }

    public List<ReviewResponseDTO> getReviews(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        return user.getReviews().stream().map(ReviewMapper::toDTO).toList();
    }
}
