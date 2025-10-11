package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.OrderItem;
import com.firomsa.ecommerce.v1.dto.OrderItemResponseDTO;

public class OrderItemMapper {

    private OrderItemMapper() {
    }

    public static OrderItemResponseDTO toDTO(OrderItem orderItem) {
        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId().toString())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
