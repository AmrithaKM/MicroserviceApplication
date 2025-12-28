package com.microservice.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDetailsResponse {
    private Long id;
    private String skuCode;
    private Integer quantity;
}
