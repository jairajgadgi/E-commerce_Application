package com.example.ecommerce_application.service;

import com.example.ecommerce_application.entity.Order;

public interface KafkaOrderEventPublisher {
    void publishOrderPlaced(Order order);
}
