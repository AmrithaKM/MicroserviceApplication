package com.microservice.orderservice.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class OrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNumber;
    private String skuCode;
    private Integer quantity;
    private BigDecimal price;
    private String status;
    private Long timestamp;
}

