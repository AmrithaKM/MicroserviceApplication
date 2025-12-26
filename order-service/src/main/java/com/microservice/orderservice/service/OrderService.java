package com.microservice.orderservice.service;


import com.microservice.orderservice.dto.OrderEvent;
import com.microservice.orderservice.dto.OrderLineItemsDto;
import com.microservice.orderservice.dto.OrderRequest;
import com.microservice.orderservice.dto.ProductResponse;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.model.OrderLineItems;
import com.microservice.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final OrderEventProducer orderEventProducer;

    public void placeOrder(OrderRequest orderRequest) {
        // Handle both simple format (skuCode, quantity) and complex format (list of items)
        if (orderRequest.getSkuCode() != null && orderRequest.getQuantity() != null) {
            // Step 1: Simple format - single item order
            placeSimpleOrder(orderRequest.getSkuCode(), orderRequest.getQuantity());
        } else if (orderRequest.getOrderLineItemsDtoList() != null && !orderRequest.getOrderLineItemsDtoList().isEmpty()) {
            // Step 2: Complex format - multiple items order
            placeComplexOrder(orderRequest.getOrderLineItemsDtoList());
        } else {
            throw new IllegalArgumentException("Invalid order request format");
        }
    }

    /**
     * Flow:
     * 1. POST /api/order with { "skuCode": "LAPTOP-001", "quantity": 2 }
     * 2. OrderService calls: GET /api/inventory?skuCode=LAPTOP-001&quantity=2
     * 3. InventoryService returns: true (stock available)
     * 7. OrderService publishes order.created event to Kafka
     * 8. Response: "Order placed Successfully"
     * 5. ProductService returns: { "id": "123", "name": "Laptop", "price": 1299.99 }
     * 6. OrderService saves order to database
     * 7. Response: "Order placed Successfully"
     */
    private void placeSimpleOrder(String skuCode, Integer quantity) {
        log.info("Step 1: Received order request - skuCode: {}, quantity: {}", skuCode, quantity);

        // Step 2: Check inventory
        log.info("Step 2: Calling InventoryService to check stock");
        Boolean isInStock = restTemplate.getForObject(
            "http://localhost:8081/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity,
            Boolean.class
        );

        // Step 3: Verify stock is available
        if (isInStock == null || !isInStock) {
            log.warn("Step 3: Order failed - Insufficient stock for skuCode: {}", skuCode);
            throw new IllegalArgumentException("Insufficient stock for product: " + skuCode);
        }
        log.info("Step 3: Stock available - Proceeding with order");

        // Step 4: Get product details (optional)
        log.info("Step 4: Calling ProductService to get product details");
        ProductResponse product = restTemplate.getForObject(
            "http://localhost:8082/api/product/" + skuCode,
            ProductResponse.class
        );

        // Step 5: Product details received
        if (product != null) {
            log.info("Step 5: Product details received - name: {}, price: {}", product.getName(), product.getPrice());
        }

        // Step 6: Save order to database
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        OrderLineItems orderLineItem = new OrderLineItems();
        orderLineItem.setSkuCode(skuCode);
        orderLineItem.setQuantity(quantity);
        if (product != null) {
            orderLineItem.setPrice(product.getPrice());
        }

        List<OrderLineItems> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        order.setOrderLineItemsList(orderLineItems);

        // Step 7: Publish order.created event to Kafka
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .skuCode(skuCode)
                .quantity(quantity)
                .price(orderLineItem.getPrice() != null ? new BigDecimal(orderLineItem.getPrice().toString()) : null)
                .status("CREATED")
                .timestamp(System.currentTimeMillis())
                .build();

        orderEventProducer.publishOrderEvent(orderEvent);
        log.info("Step 7: Order event published to Kafka - orderNumber: {}", order.getOrderNumber());

        // Step 8: Order completed successfully
        log.info("Step 8: Order placed successfully - Response: 'Order placed Successfully'");

        // Step 7: Order completed successfully
        log.info("Step 7: Order placed successfully - Response: 'Order placed Successfully'");
    }

    private void placeComplexOrder(List<OrderLineItemsDto> orderLineItemsDtoList) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderLineItemsDtoList
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        orderRepository.save(order);
        log.info("Complex order saved to database - orderNumber: {}", order.getOrderNumber());

        // Publish order.created event to Kafka for each order item
        for (OrderLineItems item : orderLineItems) {
            OrderEvent orderEvent = OrderEvent.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .skuCode(item.getSkuCode())
                    .quantity(item.getQuantity())
                    .price(item.getPrice() != null ? new BigDecimal(item.getPrice().toString()) : null)
                    .status("CREATED")
                    .timestamp(System.currentTimeMillis())
                    .build();

            orderEventProducer.publishOrderEvent(orderEvent);
            log.info("Order event published to Kafka for item: {}", item.getSkuCode());
        }

        log.info("Complex order placed successfully - orderNumber: {}", order.getOrderNumber());
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}