package com.firomsa.ecommerce.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.dto.AddressResponseDTO;
import com.firomsa.ecommerce.dto.CartResponseDTO;
import com.firomsa.ecommerce.dto.OrderResponseDTO;
import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "API for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAll();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID id) {
        UserResponseDTO user = userService.get(id);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping()
    public ResponseEntity<UserResponseDTO> addUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO user = userService.create(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserRequestDTO userRequestDTO, @PathVariable UUID id) {
        UserResponseDTO user = userService.update(userRequestDTO, id);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id, @RequestParam Optional<Boolean> force) {
        if(force.isPresent() && force.get() == true){
            userService.remove(id);
        }
        else{
            userService.softDelete(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/carts")
    public ResponseEntity<List<CartResponseDTO>> getUserCarts(@PathVariable UUID id) {
        List<CartResponseDTO> carts = userService.getCarts(id);
        return ResponseEntity.ok().body(carts);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(@PathVariable UUID id) {
        List<OrderResponseDTO> orders = userService.getOrders(id);
        return ResponseEntity.ok().body(orders);
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(@PathVariable UUID id) {
        List<AddressResponseDTO> address = userService.getAddresses(id);
        return ResponseEntity.ok().body(address);
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(@PathVariable UUID id) {
        List<ReviewResponseDTO> reviews = userService.getReviews(id);
        return ResponseEntity.ok().body(reviews);
    }
}
