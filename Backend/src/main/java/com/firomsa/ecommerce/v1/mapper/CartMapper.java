package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.v1.dto.CartRequestDTO;
import com.firomsa.ecommerce.v1.dto.CartResponseDTO;

public class CartMapper {

    private CartMapper() {
    }

    public static CartResponseDTO toDTO(Cart cart) {
        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId().toString())
                .productId(cart.getProduct().getId().toString())
                .quantity(cart.getQuantity())
                .createdAt(cart.getCreatedAt().toString())
                .updatedAt(cart.getUpdatedAt().toString())
                .build();
    }

    public static Cart toModel(CartRequestDTO cartRequestDTO) {
        return Cart.builder()
                .quantity(cartRequestDTO.getQuantity())
                .build();
    }
}
