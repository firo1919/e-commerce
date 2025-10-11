package com.firomsa.ecommerce.v1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firomsa.ecommerce.v1.dto.OrderResponseDTO;
import com.firomsa.ecommerce.v1.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "API for managing orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "For getting all orders")
    @GetMapping()
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAll();
        return ResponseEntity.ok().body(orders);
    }

    @Operation(summary = "For getting a single order")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable int id) {
        OrderResponseDTO order = orderService.get(id);
        return ResponseEntity.ok().body(order);
    }

    @Operation(summary = "For deleting a order")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        orderService.remove(id);
        return ResponseEntity.noContent().build();
    }

}
