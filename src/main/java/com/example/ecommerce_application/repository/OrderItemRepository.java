package com.example.ecommerce_application.repository;

import com.example.ecommerce_application.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
