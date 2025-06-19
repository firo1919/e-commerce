package com.firomsa.ecommerce.mapper;

import com.firomsa.ecommerce.dto.OrderRequestDTO;
import com.firomsa.ecommerce.dto.OrderResponseDTO;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.OrderStatus;

public class OrderMapper {
    public static OrderResponseDTO toDTO(Order order){
        return OrderResponseDTO.builder()
                .status(order.getStatus().toString())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt().toString())
                .orderItems(order.getOrderItems().stream().map(OrderItemMapper::toDTO).toList())
                .updatedAt(order.getUpdatedAt().toString())
                .id(order.getId())
                .userId(order.getUser().getId().toString())
                .build();
    }

    public static Order toModel(OrderRequestDTO orderRequestDTO){
        return Order.builder()
                .status(OrderStatus.valueOf(orderRequestDTO.getStatus()))
                .build();
    }
}
