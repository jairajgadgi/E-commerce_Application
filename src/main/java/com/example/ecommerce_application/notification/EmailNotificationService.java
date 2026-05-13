package com.example.ecommerce_application.notification;

import com.example.ecommerce_application.event.OrderPlacedEvent;

public interface EmailNotificationService {
    void sendWelcomeEmail(String to, String firstName);

    void sendOrderConfirmation(OrderPlacedEvent event);
}
