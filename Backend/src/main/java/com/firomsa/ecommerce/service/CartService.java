package com.firomsa.ecommerce.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.dto.CartResponseDTO;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.mapper.CartMapper;
import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.repository.CartRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository){
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
