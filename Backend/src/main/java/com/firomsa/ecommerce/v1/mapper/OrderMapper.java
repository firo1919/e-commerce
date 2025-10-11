package com.firomsa.ecommerce.v1.mapper;

import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.OrderStatus;
import com.firomsa.ecommerce.v1.dto.OrderRequestDTO;
import com.firomsa.ecommerce.v1.dto.OrderResponseDTO;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .status(order.getStatus().toString())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt().toString())
                .orderItems(order.getOrderItems().stream().map(OrderItemMapper::toDTO).toList())
                .updatedAt(order.getUpdatedAt().toString())
                .id(order.getId())
                .userId(order.getUser().getId().toString())
                .txRef(order.getTxRef())
                .build();
    }

    public static Order toModel(OrderRequestDTO orderRequestDTO) {
        return Order.builder()
                .status(OrderStatus.valueOf(orderRequestDTO.getStatus()))
                .build();
    }
}
