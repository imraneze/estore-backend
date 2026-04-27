package com.estore.review.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Author name is required")
    private String authorName;

    @Min(value = 1) @Max(value = 5)
    private int rating;

    @NotBlank(message = "Comment is required")
    private String comment;
}