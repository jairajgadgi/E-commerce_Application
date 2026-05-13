package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.dto.CartDtos.AddItemRequest;
import com.example.ecommerce_application.dto.CartDtos.CartItemResponse;
import com.example.ecommerce_application.dto.CartDtos.CartResponse;
import com.example.ecommerce_application.dto.CartDtos.UpdateItemQuantityRequest;
import com.example.ecommerce_application.entity.Cart;
import com.example.ecommerce_application.entity.CartItem;
import com.example.ecommerce_application.entity.Customer;
import com.example.ecommerce_application.entity.Product;
import com.example.ecommerce_application.exception.InsufficientStockException;
import com.example.ecommerce_application.exception.NotFoundException;
import com.example.ecommerce_application.repository.CartItemRepository;
import com.example.ecommerce_application.repository.CartRepository;
import com.example.ecommerce_application.repository.CustomerRepository;
import com.example.ecommerce_application.repository.ProductRepository;
import com.example.ecommerce_application.service.CartService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           CustomerRepository customerRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CartResponse getCart(Long customerId) {
        return toResponse(getOrCreateCart(customerId));
    }

    @Override
    public CartResponse addItem(Long customerId, AddItemRequest request) {
        Cart cart = getOrCreateCart(customerId);
        Product product = findProduct(request.productId());
        ensureStockAvailable(product, request.quantity());

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setUnitPrice(product.getPrice());
                    newItem.setQuantity(0);
                    return newItem;
                });

        int newQuantity = item.getQuantity() + request.quantity();
        ensureStockAvailable(product, newQuantity);
        item.setQuantity(newQuantity);
        item.setUnitPrice(product.getPrice());
        cart.setCheckedOut(false);
        cartItemRepository.save(item);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override
    public CartResponse updateItemQuantity(Long customerId, Long itemId, UpdateItemQuantityRequest request) {
        Cart cart = getOrCreateCart(customerId);
        CartItem item = findCartItem(cart.getId(), itemId);
        Product product = item.getProduct();
        ensureStockAvailable(product, request.quantity());
        item.setQuantity(request.quantity());
        item.setUnitPrice(product.getPrice());
        cart.setCheckedOut(false);
        cartItemRepository.save(item);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override
    public CartResponse removeItem(Long customerId, Long itemId) {
        Cart cart = getOrCreateCart(customerId);
        CartItem item = findCartItem(cart.getId(), itemId);
        cartItemRepository.delete(item);
        return toResponse(cart);
    }

    @Override
    public void clearCart(Long customerId) {
        Cart cart = getOrCreateCart(customerId);
        cartItemRepository.deleteAll(cartItemRepository.findByCartId(cart.getId()));
        cart.setCheckedOut(false);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));
        return cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomer(customer);
                    return cartRepository.save(cart);
                });
    }

    private CartItem findCartItem(Long cartId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found: " + itemId));
        if (item.getCart() == null || item.getCart().getId() == null || !item.getCart().getId().equals(cartId)) {
            throw new NotFoundException("Cart item not found in this cart: " + itemId);
        }
        return item;
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
    }

    private void ensureStockAvailable(Product product, int quantity) {
        if (quantity <= 0) {
            throw new InsufficientStockException("Quantity must be greater than zero");
        }
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product " + product.getSku());
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getId()).stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getSku(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getCustomer().getId(),
                cart.getCustomer().getEmail(),
                items,
                totalAmount);
    }
}
