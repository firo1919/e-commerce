package com.firomsa.ecommerce.mapper;

import com.firomsa.ecommerce.dto.OrderItemResponseDTO;
import com.firomsa.ecommerce.model.OrderItem;

public class OrderItemMapper {
    public static OrderItemResponseDTO toDTO(OrderItem orderItem){
        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId().toString())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
