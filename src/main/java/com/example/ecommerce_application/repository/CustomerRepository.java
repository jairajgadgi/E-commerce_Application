package com.example.ecommerce_application.repository;

import com.example.ecommerce_application.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmailIgnoreCase(String email);
}
