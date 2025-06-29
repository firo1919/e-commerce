package com.firomsa.ecommerce.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.firomsa.ecommerce.dto.AddressRequestDTO;
import com.firomsa.ecommerce.dto.AddressResponseDTO;
import com.firomsa.ecommerce.dto.CartRequestDTO;
import com.firomsa.ecommerce.dto.CartResponseDTO;
import com.firomsa.ecommerce.dto.OrderResponseDTO;
import com.firomsa.ecommerce.dto.ReviewRequestDTO;
import com.firomsa.ecommerce.dto.ReviewResponseDTO;
import com.firomsa.ecommerce.dto.UserRequestDTO;
import com.firomsa.ecommerce.dto.UserResponseDTO;
import com.firomsa.ecommerce.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "For getting all users")
    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAll();
        return ResponseEntity.ok().body(users);
    }

    @Operation(summary = "For getting a single user")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID id) {
        UserResponseDTO user = userService.get(id);
        return ResponseEntity.ok().body(user);
    }

    @Operation(summary = "For updating a user")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserRequestDTO userRequestDTO,
            @PathVariable UUID id) {
        UserResponseDTO user = userService.update(userRequestDTO, id);
        return ResponseEntity.ok().body(user);
    }

    @Operation(summary = "For deleting a user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id, @RequestParam Optional<Boolean> force) {
        force.ifPresentOrElse(val -> {
            if (val) {
                userService.remove(id);
            } else {
                userService.softDelete(id);
            }
        }, () -> userService.softDelete(id));

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "For getting all users cartItems")
    @GetMapping("/{id}/carts")
    public ResponseEntity<List<CartResponseDTO>> getUserCarts(@PathVariable UUID id) {
        List<CartResponseDTO> carts = userService.getCarts(id);
        return ResponseEntity.ok().body(carts);
    }

    @Operation(summary = "For adding a cartItem to users cart")
    @PostMapping("/{id}/carts")
    public ResponseEntity<CartResponseDTO> addItemToUserCart(@Valid @RequestBody CartRequestDTO cartRequestDTO,
            @PathVariable UUID id, @RequestParam UUID productId) {
        CartResponseDTO cart = userService.addItemToCart(id, cartRequestDTO, productId);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/{id}")
                .buildAndExpand(cart.getId()).toUri();
        return ResponseEntity.created(location).body(cart);
    }

    @Operation(summary = "For getting all users orders")
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(@PathVariable UUID id) {
        List<OrderResponseDTO> orders = userService.getOrders(id);
        return ResponseEntity.ok().body(orders);
    }

    @Operation(summary = "For getting all users addresses")
    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(@PathVariable UUID id) {
        List<AddressResponseDTO> address = userService.getAddresses(id);
        return ResponseEntity.ok().body(address);
    }

    @Operation(summary = "For adding an address to users addresses")
    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponseDTO> addAddressToUserAddresses(
            @Valid @RequestBody AddressRequestDTO addressRequestDTO,
            @PathVariable UUID id) {
        AddressResponseDTO address = userService.addAddressToAddresses(id, addressRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/addresses/{id}")
                .buildAndExpand(address.getId()).toUri();
        return ResponseEntity.created(location).body(address);
    }

    @Operation(summary = "For getting all users reviews")
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(@PathVariable UUID id) {
        List<ReviewResponseDTO> reviews = userService.getReviews(id);
        return ResponseEntity.ok().body(reviews);
    }

    @Operation(summary = "For adding a review to users reviews")
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponseDTO> addReviewToUserReviews(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @PathVariable UUID id, @RequestParam UUID productId) {
        ReviewResponseDTO review = userService.addReviewToReviews(id, reviewRequestDTO, productId);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/reviews/{id}")
                .buildAndExpand(review.getId()).toUri();
        return ResponseEntity.created(location).body(review);
    }
}
