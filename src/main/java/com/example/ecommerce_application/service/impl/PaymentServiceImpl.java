package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.entity.Order;
import com.example.ecommerce_application.entity.PaymentMethod;
import com.example.ecommerce_application.entity.PaymentStatus;
import com.example.ecommerce_application.entity.PaymentTransaction;
import com.example.ecommerce_application.repository.PaymentTransactionRepository;
import com.example.ecommerce_application.service.PaymentService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentServiceImpl(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Override
    public PaymentTransaction processPayment(Order order) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderNumber(order.getOrderNumber());
        transaction.setProvider(determineProvider(order.getPaymentMethod()));
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setAmount(order.getTotalAmount());
        transaction.setStatus(order.getPaymentMethod() == PaymentMethod.COD ? PaymentStatus.PENDING : PaymentStatus.SUCCESS);
        return paymentTransactionRepository.save(transaction);
    }

    private String determineProvider(PaymentMethod method) {
        return switch (method) {
            case CARD -> "Stripe";
            case UPI -> "Razorpay";
            case WALLET -> "Paytm";
            case COD -> "CashOnDelivery";
        };
    }
}
