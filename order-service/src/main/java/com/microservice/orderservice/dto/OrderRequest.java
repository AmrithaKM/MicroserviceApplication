package com.microservice.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    // Simple format for single item orders
    private String skuCode;
    private Integer quantity;

    // Complex format for multiple items
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}