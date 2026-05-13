package com.example.ecommerce_application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public final class ProductDtos {

    private ProductDtos() {
    }

    public record ProductRequest(
            @NotBlank String sku,
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            @Min(0) int stockQuantity,
            @NotNull Long categoryId,
            Boolean active
    ) {
    }

    public record ProductResponse(
            Long id,
            String sku,
            String name,
            String description,
            BigDecimal price,
            int stockQuantity,
            boolean active,
            CategoryDtos.CategoryResponse category
    ) {
    }
}
