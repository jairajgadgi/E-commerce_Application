package com.example.ecommerce_application.service;

import com.example.ecommerce_application.entity.Order;
import com.example.ecommerce_application.entity.PaymentTransaction;

public interface PaymentService {
    PaymentTransaction processPayment(Order order);
}
