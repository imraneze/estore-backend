package com.estore.billing.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PlaceOrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;
}