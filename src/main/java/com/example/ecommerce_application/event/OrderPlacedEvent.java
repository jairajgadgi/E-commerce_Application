package com.example.ecommerce_application.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderPlacedEvent(
        String orderNumber,
        Long customerId,
        String customerEmail,
        BigDecimal totalAmount,
        List<OrderLine> items,
        Instant placedAt
) {
    public record OrderLine(Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
    }
}
