package com.example.ecommerce_application.controller;

import com.example.ecommerce_application.dto.CustomerDtos.AddressRequest;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerRequest;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerResponse;
import com.example.ecommerce_application.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<Void> addAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        customerService.addAddress(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
