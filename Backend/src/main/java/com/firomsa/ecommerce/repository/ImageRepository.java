package com.firomsa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firomsa.ecommerce.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>{
    
}
