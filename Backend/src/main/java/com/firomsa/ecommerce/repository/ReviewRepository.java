package com.firomsa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer>{

}
