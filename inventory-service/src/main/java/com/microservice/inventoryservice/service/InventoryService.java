package com.microservice.inventoryservice.service;


import com.microservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
}