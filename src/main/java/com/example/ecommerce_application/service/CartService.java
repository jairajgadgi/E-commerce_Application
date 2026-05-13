package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.CartDtos.AddItemRequest;
import com.example.ecommerce_application.dto.CartDtos.CartResponse;
import com.example.ecommerce_application.dto.CartDtos.UpdateItemQuantityRequest;

public interface CartService {
    CartResponse getCart(Long customerId);

    CartResponse addItem(Long customerId, AddItemRequest request);

    CartResponse updateItemQuantity(Long customerId, Long itemId, UpdateItemQuantityRequest request);

    CartResponse removeItem(Long customerId, Long itemId);

    void clearCart(Long customerId);
}
