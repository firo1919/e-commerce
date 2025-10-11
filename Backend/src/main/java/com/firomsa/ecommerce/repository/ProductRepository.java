package com.firomsa.ecommerce.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

}
