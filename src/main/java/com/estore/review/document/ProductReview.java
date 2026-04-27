package com.estore.review.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "product_reviews")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductReview {

    @Id
    private String id;

    private Long productId;
    private Long userId;
    private String authorName;

    private int rating;         // 1–5
    private String comment;
    private LocalDateTime createdAt;
}