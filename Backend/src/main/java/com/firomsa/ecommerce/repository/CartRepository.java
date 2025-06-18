package com.firomsa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

}
