package com.microservice.inventoryservice.controller;
import com.microservice.inventoryservice.model.Inventory;
import com.microservice.inventoryservice.dto.CreateInventoryRequest;
import com.microservice.inventoryservice.dto.UpdateInventoryRequest;
import com.microservice.inventoryservice.dto.InventoryDetailsResponse;
import com.microservice.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        return inventoryService.isInStock(skuCode, quantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventory createInventory(@RequestBody CreateInventoryRequest request) {
        log.info("Creating inventory for SKU: {}", request.getSkuCode());
        return inventoryService.createInventory(request.getSkuCode(), request.getQuantity());
    }

    @PutMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public Inventory updateInventoryQuantity(@PathVariable String skuCode, @RequestBody UpdateInventoryRequest request) {
        log.info("Updating inventory for SKU: {} with new quantity: {}", skuCode, request.getQuantity());
        return inventoryService.updateInventoryQuantity(skuCode, request.getQuantity());
    }

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryDetailsResponse getInventoryBySkuCode(@PathVariable String skuCode) {
        log.info("Fetching inventory details for SKU: {}", skuCode);
        return inventoryService.getInventoryBySkuCode(skuCode);
    }
}
