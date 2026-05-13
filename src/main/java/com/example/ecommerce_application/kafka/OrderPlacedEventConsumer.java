package com.example.ecommerce_application.kafka;

import com.example.ecommerce_application.event.OrderPlacedEvent;
import com.example.ecommerce_application.notification.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacedEventConsumer.class);
    private final EmailNotificationService emailNotificationService;

    public OrderPlacedEventConsumer(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handle(OrderPlacedEvent event) {
        log.info("Consumed order event for order {}", event.orderNumber());
        emailNotificationService.sendOrderConfirmation(event);
    }
}
