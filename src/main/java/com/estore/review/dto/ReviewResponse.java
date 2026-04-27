package com.estore.review.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReviewResponse {
    private String id;
    private Long productId;
    private Long userId;
    private String authorName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}