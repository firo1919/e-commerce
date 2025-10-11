package com.firomsa.ecommerce.v1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firomsa.ecommerce.exception.OrderProcessException;
import com.firomsa.ecommerce.exception.ResourceNotFoundException;
import com.firomsa.ecommerce.model.Order;
import com.firomsa.ecommerce.model.OrderStatus;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.repository.OrderRepository;
import com.firomsa.ecommerce.repository.ProductRepository;
import com.firomsa.ecommerce.v1.dto.OrderResponseDTO;
import com.firomsa.ecommerce.v1.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponseDTO> getAll() {
        return orderRepository.findAll().stream().map(OrderMapper::toDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDTO get(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order: " + id));
        return OrderMapper.toDTO(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void remove(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order: " +
                        id));
        orderRepository.delete(order);
    }

    @Transactional
    public void updateStatus(String status, String tx_ref) {
        Order order = orderRepository.findByTxRef(tx_ref).orElseThrow(() -> new ResourceNotFoundException("Order: " +
                tx_ref));
        log.info("Updating order [{}] to status [{}]", tx_ref, status);
        if (status.equals("success")) {
            order.setStatus(OrderStatus.PAID);
            Map<Product, Integer> productsMap = order.getOrderItems().stream()
                    .map(orderItem -> Map.entry(orderItem.getProduct(), orderItem.getQuantity()))
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
            List<Product> products = new ArrayList<>();
            productsMap.forEach((pro, quantity) -> {
                pro.setStock(pro.getStock() - quantity);
                if (pro.getStock() < 0) {
                    throw new OrderProcessException("Product Stock Limited");
                }
                products.add(pro);
            });

            productRepository.saveAll(products);
            orderRepository.save(order);

        } else if (status.equals("failed/cancelled")) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        } else {
            log.warn("Unknown payment status [{}] received for tx_ref [{}]", status, tx_ref);
        }
    }
}
