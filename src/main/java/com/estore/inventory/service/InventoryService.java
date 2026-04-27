package com.estore.inventory.service;

import com.estore.catalog.entity.Product;
import com.estore.catalog.repository.ProductRepository;
import com.estore.exception.*;
import com.estore.inventory.dto.*;
import com.estore.inventory.entity.Inventory;
import com.estore.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryResponse getByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        return toResponse(inventory);
    }

    public InventoryResponse setStock(InventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElse(Inventory.builder().product(product).build());

        inventory.setQuantity(request.getQuantity());
        return toResponse(inventoryRepository.save(inventory));
    }

    // called internally by shopping/billing services
    public void reduceStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock for product id: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    public boolean isAvailable(Long productId, int quantity) {
        return inventoryRepository.findByProductId(productId)
                .map(inv -> inv.getQuantity() >= quantity)
                .orElse(false);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .build();
    }
}