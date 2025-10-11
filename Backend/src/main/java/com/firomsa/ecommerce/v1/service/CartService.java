package com.firomsa.ecommerce.v1.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.repository.CartRepository;
import com.firomsa.ecommerce.v1.dto.CartResponseDTO;
import com.firomsa.ecommerce.v1.mapper.CartMapper;

@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CartResponseDTO> getAll() {
        return cartRepository.findAll().stream().map(CartMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CartResponseDTO get(int id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart: " + id));
        return CartMapper.toDTO(cart);
    }

    @PreAuthorize("hasRole('USER')")
    public void remove(int id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart: " +
                        id));
        cartRepository.delete(cart);
    }
}
