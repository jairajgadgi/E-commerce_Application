package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.entity.Order;
import com.example.ecommerce_application.event.OrderPlacedEvent;
import com.example.ecommerce_application.service.KafkaOrderEventPublisher;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderEventPublisherImpl implements KafkaOrderEventPublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String topic;

    public KafkaOrderEventPublisherImpl(KafkaTemplate<Object, Object> kafkaTemplate,
                                        @Value("${app.kafka.topics.order-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publishOrderPlaced(Order order) {
        List<OrderPlacedEvent.OrderLine> lines = order.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderLine(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()))
                .toList();

        OrderPlacedEvent event = new OrderPlacedEvent(
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getEmail(),
                order.getTotalAmount(),
                lines,
                order.getCreatedAt());
        kafkaTemplate.send(topic, order.getOrderNumber(), event);
    }
}
