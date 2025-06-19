package com.firomsa.ecommerce.mapper;

import com.firomsa.ecommerce.dto.CartRequestDTO;
import com.firomsa.ecommerce.dto.CartResponseDTO;
import com.firomsa.ecommerce.model.Cart;

public class CartMapper {
    public static CartResponseDTO toDTO(Cart cart){
        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId().toString())
                .productId(cart.getProduct().getId().toString())
                .quantity(cart.getQuantity())
                .createdAt(cart.getCreatedAt().toString())
                .updatedAt(cart.getUpdatedAt().toString())
                .build();
    }

    public static Cart toModel(CartRequestDTO cartRequestDTO){
        return Cart.builder()
                .quantity(cartRequestDTO.getQuantity())
                .build();
    }
}
