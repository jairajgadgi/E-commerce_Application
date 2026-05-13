package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.OrderDtos.CheckoutRequest;
import com.example.ecommerce_application.dto.OrderDtos.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse checkout(CheckoutRequest request);

    OrderResponse getByOrderNumber(String orderNumber);

    List<OrderResponse> getByCustomerId(Long customerId);
}
