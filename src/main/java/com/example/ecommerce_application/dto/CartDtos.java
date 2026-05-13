package com.example.ecommerce_application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public final class CartDtos {

    private CartDtos() {
    }

    public record AddItemRequest(
            @NotNull Long productId,
            @Min(1) int quantity
    ) {
    }

    public record UpdateItemQuantityRequest(@Min(1) int quantity) {
    }

    public record CartItemResponse(
            Long itemId,
            Long productId,
            String sku,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
    }

    public record CartResponse(
            Long cartId,
            Long customerId,
            String customerEmail,
            List<CartItemResponse> items,
            BigDecimal totalAmount
    ) {
    }
}
