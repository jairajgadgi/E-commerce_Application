package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.dto.OrderDtos.CheckoutRequest;
import com.example.ecommerce_application.dto.OrderDtos.OrderItemResponse;
import com.example.ecommerce_application.dto.OrderDtos.OrderResponse;
import com.example.ecommerce_application.entity.Cart;
import com.example.ecommerce_application.entity.CartItem;
import com.example.ecommerce_application.entity.Customer;
import com.example.ecommerce_application.entity.Order;
import com.example.ecommerce_application.entity.OrderItem;
import com.example.ecommerce_application.entity.OrderStatus;
import com.example.ecommerce_application.entity.PaymentStatus;
import com.example.ecommerce_application.entity.PaymentTransaction;
import com.example.ecommerce_application.entity.Product;
import com.example.ecommerce_application.exception.BadRequestException;
import com.example.ecommerce_application.exception.InsufficientStockException;
import com.example.ecommerce_application.exception.NotFoundException;
import com.example.ecommerce_application.repository.CartItemRepository;
import com.example.ecommerce_application.repository.CartRepository;
import com.example.ecommerce_application.repository.CustomerRepository;
import com.example.ecommerce_application.repository.OrderRepository;
import com.example.ecommerce_application.repository.ProductRepository;
import com.example.ecommerce_application.service.KafkaOrderEventPublisher;
import com.example.ecommerce_application.service.OrderService;
import com.example.ecommerce_application.service.PaymentService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final KafkaOrderEventPublisher eventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository,
                            PaymentService paymentService,
                            KafkaOrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public OrderResponse checkout(CheckoutRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.customerId()));
        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new NotFoundException("Cart not found for customer: " + customer.getId()));
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setPaymentMethod(request.paymentMethod());
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingAddressLine1(request.shippingAddress().line1());
        order.setShippingAddressLine2(request.shippingAddress().line2());
        order.setShippingCity(request.shippingAddress().city());
        order.setShippingState(request.shippingAddress().state());
        order.setShippingZipCode(request.shippingAddress().zipCode());
        order.setShippingCountry(request.shippingAddress().country());

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product " + product.getSku());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            BigDecimal lineTotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(lineTotal);
            order.getItems().add(orderItem);
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        PaymentTransaction paymentTransaction = paymentService.processPayment(savedOrder);
        savedOrder.setPaymentStatus(paymentTransaction.getStatus());
        savedOrder.setStatus(paymentTransaction.getStatus() == PaymentStatus.SUCCESS ? OrderStatus.PAID : OrderStatus.CREATED);
        orderRepository.save(savedOrder);
        eventPublisher.publishOrderPlaced(savedOrder);

        cartItemRepository.deleteAll(cartItems);
        cart.setCheckedOut(true);
        cartRepository.save(cart);

        return toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderNumber));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByCustomerId(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getEmail(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getTotalAmount(),
                shippingAddress(order),
                itemResponses,
                order.getCreatedAt());
    }

    private String shippingAddress(Order order) {
        return String.join(", ", List.of(
                order.getShippingAddressLine1(),
                order.getShippingAddressLine2(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingZipCode(),
                order.getShippingCountry()).stream().filter(value -> value != null && !value.isBlank()).toList());
    }
}
