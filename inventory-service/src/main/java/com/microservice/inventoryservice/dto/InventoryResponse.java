package com.microservice.inventoryservice.dto;

public record InventoryResponse(String skuCode, boolean isInStock) {
}