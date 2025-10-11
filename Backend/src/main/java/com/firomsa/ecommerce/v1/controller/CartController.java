package com.firomsa.ecommerce.v1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.v1.dto.CartResponseDTO;
import com.firomsa.ecommerce.v1.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart", description = "API for managing carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "For getting all carts")
    @GetMapping()
    public ResponseEntity<List<CartResponseDTO>> getAllCarts() {
        List<CartResponseDTO> carts = cartService.getAll();
        return ResponseEntity.ok().body(carts);
    }

    @Operation(summary = "For getting a single cart")
    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable int id) {
        CartResponseDTO cart = cartService.get(id);
        return ResponseEntity.ok().body(cart);
    }

    @Operation(summary = "For deleting a cart")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable int id) {
        cartService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
