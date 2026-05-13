package com.example.ecommerce_application.repository;

import com.example.ecommerce_application.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String searchTerm);

    List<Product> findByCategoryId(Long categoryId);
}
