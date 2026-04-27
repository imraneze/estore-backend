package com.estore.inventory.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
}