package com.example.ecommerce_application.controller;

import com.example.ecommerce_application.dto.CartDtos.AddItemRequest;
import com.example.ecommerce_application.dto.CartDtos.CartResponse;
import com.example.ecommerce_application.dto.CartDtos.UpdateItemQuantityRequest;
import com.example.ecommerce_application.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartResponse> addItem(@PathVariable Long customerId, @Valid @RequestBody AddItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(customerId, request));
    }

    @PutMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(@PathVariable Long customerId,
                                                           @PathVariable Long itemId,
                                                           @Valid @RequestBody UpdateItemQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(customerId, itemId, request));
    }

    @DeleteMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long customerId, @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(customerId, itemId));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
