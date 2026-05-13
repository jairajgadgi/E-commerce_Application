package com.example.ecommerce_application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class CustomerDtos {

    private CustomerDtos() {
    }

    public record CustomerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email @NotBlank String email,
            String phone
    ) {
    }

    public record CustomerResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone
    ) {
    }

    public record AddressRequest(
            @NotBlank String label,
            @NotBlank String line1,
            String line2,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String zipCode,
            @NotBlank String country,
            Boolean defaultAddress
    ) {
    }
}
