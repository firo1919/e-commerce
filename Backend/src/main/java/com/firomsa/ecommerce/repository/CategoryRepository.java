package com.firomsa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
