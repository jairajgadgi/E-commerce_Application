package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.CustomerDtos.AddressRequest;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerRequest;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerResponse;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);

    CustomerResponse getById(Long id);

    void addAddress(Long customerId, AddressRequest request);
}
