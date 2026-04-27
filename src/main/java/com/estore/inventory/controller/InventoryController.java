package com.estore.inventory.controller;

import com.estore.inventory.dto.*;
import com.estore.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> setStock(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.setStock(request));
    }
}