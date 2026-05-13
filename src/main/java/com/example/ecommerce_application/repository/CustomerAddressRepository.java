package com.example.ecommerce_application.repository;

import com.example.ecommerce_application.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
	boolean existsByCustomerId(Long customerId);
}
