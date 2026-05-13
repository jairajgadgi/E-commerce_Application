package com.example.ecommerce_application.controller;

import com.example.ecommerce_application.dto.AuthDtos.AuthResponse;
import com.example.ecommerce_application.dto.AuthDtos.LoginRequest;
import com.example.ecommerce_application.dto.AuthDtos.RegisterCustomerRequest;
import com.example.ecommerce_application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<AuthResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerCustomer(request));
    }
}
