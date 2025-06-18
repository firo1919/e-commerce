package com.firomsa.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firomsa.ecommerce.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer>{

}
