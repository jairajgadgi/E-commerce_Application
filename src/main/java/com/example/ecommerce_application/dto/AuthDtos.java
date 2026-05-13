package com.example.ecommerce_application.dto;

import com.example.ecommerce_application.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record RegisterCustomerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email @NotBlank String email,
            String phone,
            @NotBlank String password
    ) {
    }

    public record AuthResponse(
            String token,
            String username,
            Role role
    ) {
    }
}
