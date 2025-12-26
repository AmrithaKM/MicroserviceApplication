package com.microservice.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.orderservice.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.name:order-created-topic}")
    private String topicName;

    public void publishOrderEvent(OrderEvent orderEvent) {
        try {
            String orderEventJson = objectMapper.writeValueAsString(orderEvent);
            log.info("Publishing order event to Kafka topic '{}': {}", topicName, orderEventJson);

            Message<String> message = MessageBuilder
                    .withPayload(orderEventJson)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .setHeader("key", orderEvent.getOrderNumber())
                    .build();

            kafkaTemplate.send(message);
            log.info("Order event published successfully for order: {}", orderEvent.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to publish order event for order: {}", orderEvent.getOrderNumber(), e);
            throw new RuntimeException("Failed to publish order event", e);
        }
    }
}
