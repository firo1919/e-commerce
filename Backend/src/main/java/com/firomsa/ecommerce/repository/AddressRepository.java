package com.firomsa.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Address;
import com.firomsa.ecommerce.model.User;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByUserAndActive(User user, Boolean active);
}
