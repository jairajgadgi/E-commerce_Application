package com.example.ecommerce_application.controller;

import com.example.ecommerce_application.dto.OrderDtos.CheckoutRequest;
import com.example.ecommerce_application.dto.OrderDtos.OrderResponse;
import com.example.ecommerce_application.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(request));
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getByOrderNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getByOrderNumber(orderNumber));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getByCustomerId(customerId));
    }
}
