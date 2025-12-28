package com.microservice.inventoryservice.service;


import com.microservice.inventoryservice.model.Inventory;
import com.microservice.inventoryservice.dto.InventoryDetailsResponse;
import com.microservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        return inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
    }

    @Transactional
    public void decrementStock(String skuCode, int quantity) {
        var inventoryOpt = inventoryRepository.findAll().stream()
            .filter(inv -> skuCode.equals(inv.getSkuCode()))
            .findFirst();
        if (inventoryOpt.isPresent()) {
            var inventory = inventoryOpt.get();
            if (inventory.getQuantity() >= quantity) {
                inventory.setQuantity(inventory.getQuantity() - quantity);
                inventoryRepository.save(inventory);
            } else {
                throw new IllegalArgumentException("Not enough stock for SKU: " + skuCode);
            }
        } else {
            throw new IllegalArgumentException("SKU not found: " + skuCode);
        }
    }

    @Transactional
    public Inventory createInventory(String skuCode, Integer quantity) {
        log.info("Creating inventory for SKU: {} with quantity: {}", skuCode, quantity);

        // Check if inventory already exists
        var existingInventory = inventoryRepository.findAll().stream()
            .filter(inv -> skuCode.equals(inv.getSkuCode()))
            .findFirst();

        if (existingInventory.isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for SKU: " + skuCode);
        }

        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(quantity);

        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Inventory created successfully for SKU: {}", skuCode);
        return savedInventory;
    }

    @Transactional
    public Inventory updateInventoryQuantity(String skuCode, Integer newQuantity) {
        log.info("Updating inventory quantity for SKU: {} to: {}", skuCode, newQuantity);

        var inventory = inventoryRepository.findAll().stream()
            .filter(inv -> skuCode.equals(inv.getSkuCode()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Inventory not found for SKU: " + skuCode));

        inventory.setQuantity(newQuantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Inventory quantity updated successfully for SKU: {}", skuCode);
        return updatedInventory;
    }

    @Transactional(readOnly = true)
    public InventoryDetailsResponse getInventoryBySkuCode(String skuCode) {
        log.info("Fetching inventory details for SKU: {}", skuCode);

        var inventory = inventoryRepository.findAll().stream()
            .filter(inv -> skuCode.equals(inv.getSkuCode()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Inventory not found for SKU: " + skuCode));

        return new InventoryDetailsResponse(inventory.getId(), inventory.getSkuCode(), inventory.getQuantity());
    }
}