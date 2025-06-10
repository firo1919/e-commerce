package com.firomsa.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAll(){
        return userRepository.findAll();
    }

    public Optional<User> get(int id) {
        return userRepository.findById(id);
    }

    public User add(User user){
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> update(User newUser, int id){
        Optional<User> user = this.get(id);
        if(user.isPresent()){
            User existingUser = user.get();
            newUser.setId(id);
            newUser.setCreatedAt(existingUser.getCreatedAt());
            newUser.setUpdatedAt(LocalDateTime.now());
            return Optional.of(userRepository.save(newUser));
        }

        return Optional.empty();
    }

    public boolean remove(int id){
        Optional<User> user = this.get(id);
        if(user.isPresent()){
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

    public Optional<User> partialUpdate(Map<String, Object> userDetails, int id) {
        Optional<User> user = this.get(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            userDetails.forEach((key, value) -> {
                switch (key) {
                    case "userName":
                        existingUser.setUserName((String) value);
                        break;
                    case "email":
                        existingUser.setEmail((String) value);
                        break;
                    case "password":
                        existingUser.setPassword((String) value);
                        break;
                    case "firstName":
                        existingUser.setFirstName((String) value);
                        break;
                    case "lastName":
                        existingUser.setLastName((String) value);
                        break;
                }
            });

            existingUser.setUpdatedAt(LocalDateTime.now());
            return Optional.of(userRepository.save(existingUser));
        }
        return Optional.empty();
    }
}
