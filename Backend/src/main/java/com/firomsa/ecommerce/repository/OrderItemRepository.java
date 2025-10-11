package com.firomsa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer>{

}
