package com.firomsa.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByTxRef(String txRef);
}
