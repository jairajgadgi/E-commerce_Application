package com.example.ecommerce_application.notification;

import com.example.ecommerce_application.event.OrderPlacedEvent;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailNotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.fromAddress = "no-reply@ecommerce.local";
    }

    @Override
    public void sendWelcomeEmail(String to, String firstName) {
        sendMail(to, "Welcome to the E-Commerce Application", "Hi " + firstName + ",\n\nYour customer account has been created successfully.");
    }

    @Override
    public void sendOrderConfirmation(OrderPlacedEvent event) {
        String itemSummary = event.items().stream()
                .map(item -> item.productName() + " x" + item.quantity() + " = " + item.totalPrice())
                .collect(Collectors.joining("\n"));
        sendMail(event.customerEmail(),
                "Order Confirmation - " + event.orderNumber(),
                "Thanks for shopping with us!\n\nOrder: " + event.orderNumber() + "\nTotal: " + event.totalAmount() +
                        "\n\nItems:\n" + itemSummary + "\n\nPlaced at: " + event.placedAt());
    }

    private void sendMail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception exception) {
            log.warn("Email delivery failed for {}: {}", to, exception.getMessage());
        }
    }
}
