package com.example.ecommerce_application.dto;

import com.example.ecommerce_application.entity.PaymentMethod;
import com.example.ecommerce_application.entity.OrderStatus;
import com.example.ecommerce_application.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {

    private OrderDtos() {
    }

    public record ShippingAddressRequest(
            @NotBlank String line1,
            String line2,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String zipCode,
            @NotBlank String country
    ) {
    }

    public record CheckoutRequest(
            @NotNull Long customerId,
            @NotNull PaymentMethod paymentMethod,
            @NotNull ShippingAddressRequest shippingAddress
    ) {
    }

    public record OrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
    }

    public record OrderResponse(
            Long orderId,
            String orderNumber,
            Long customerId,
            String customerEmail,
            OrderStatus status,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            BigDecimal totalAmount,
            String shippingAddress,
            List<OrderItemResponse> items,
            Instant createdAt
    ) {
    }
}
