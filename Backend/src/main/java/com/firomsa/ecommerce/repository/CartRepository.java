package com.firomsa.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Cart;
import com.firomsa.ecommerce.model.Product;
import com.firomsa.ecommerce.model.User;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserAndProduct(User user, Product product);

    void deleteAllByUser(User user);

    List<Cart> findAllByUser(User user);
}
