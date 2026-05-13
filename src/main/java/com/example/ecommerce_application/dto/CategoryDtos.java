package com.example.ecommerce_application.dto;

import jakarta.validation.constraints.NotBlank;

public final class CategoryDtos {

    private CategoryDtos() {
    }

    public record CategoryRequest(
            @NotBlank String name,
            String description,
            Boolean active
    ) {
    }

    public record CategoryResponse(
            Long id,
            String name,
            String description,
            boolean active
    ) {
    }
}
