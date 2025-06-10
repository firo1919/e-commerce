package com.firomsa.ecommerce.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.model.User;
import com.firomsa.ecommerce.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<List<User>>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id){
        Optional<User> user = userService.get(id);
        if(user.isPresent()){
            return new ResponseEntity<User>(user.get(), HttpStatus.OK);
        }

        return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user){
        return new ResponseEntity<>(userService.add(user), HttpStatus.OK);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> partialUpdateUser(@RequestBody Map<String, Object> userDetails, @PathVariable int id){
        Optional<User> u = userService.partialUpdate(userDetails, id);
        if(u.isPresent()){
            return new ResponseEntity<User>(u.get(), HttpStatus.OK);
        }
        return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id){
        if(userService.remove(id)){
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable int id){
        Optional<User> u = userService.update(user, id);
        if(u.isPresent()){
            return new ResponseEntity<User>(u.get(), HttpStatus.OK);
        }
        return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
}
}
