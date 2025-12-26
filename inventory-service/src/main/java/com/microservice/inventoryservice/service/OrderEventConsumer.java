package com.microservice.inventoryservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(
        topics = "${spring.kafka.topic.name:order-created-topic}",
        groupId = "${spring.kafka.consumer.group-id:inventory-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreatedEvent(String message) {
        try {
            log.info("Received order.created event from Kafka: {}", message);

            // Day 1: Simple logging
            log.info("✓ Order event received and logged successfully");
            log.info("Event Message: {}", message);

            // Parse the event to extract details
            OrderEventPayload event = objectMapper.readValue(message, OrderEventPayload.class);
            log.info("Order Details - OrderNumber: {}, SKU: {}, Quantity: {}",
                event.getOrderNumber(), event.getSkuCode(), event.getQuantity());

            // TODO: Implement stock decrement logic in future steps
            inventoryService.decrementStock(event.getSkuCode(), event.getQuantity());

            log.info("✓ Order event processed successfully for orderNumber: {}", event.getOrderNumber());

        } catch (Exception e) {
            log.error("Error processing order.created event: {}", message, e);
        }
    }

    /**
     * Inner class for deserializing order event payload
     */
    public static class OrderEventPayload {
        public Long orderId;
        public String orderNumber;
        public String skuCode;
        public Integer quantity;
        public Double price;
        public String status;
        public Long timestamp;

        public Long getOrderId() { return orderId; }
        public String getOrderNumber() { return orderNumber; }
        public String getSkuCode() { return skuCode; }
        public Integer getQuantity() { return quantity; }
        public Double getPrice() { return price; }
        public String getStatus() { return status; }
        public Long getTimestamp() { return timestamp; }
    }
}
