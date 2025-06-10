package com.firomsa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.firomsa.ecommerce.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
