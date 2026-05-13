package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.AuthDtos.AuthResponse;
import com.example.ecommerce_application.dto.AuthDtos.LoginRequest;
import com.example.ecommerce_application.dto.AuthDtos.RegisterCustomerRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse registerCustomer(RegisterCustomerRequest request);
}
