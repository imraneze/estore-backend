package com.estore.review.service;

import com.estore.review.document.ProductReview;
import com.estore.review.dto.*;
import com.estore.review.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductReviewRepository reviewRepository;

    public ReviewResponse createReview(ReviewRequest request) {
        ProductReview review = ProductReview.builder()
                .productId(request.getProductId())
                .userId(request.getUserId())
                .authorName(request.getAuthorName())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(ProductReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .authorName(review.getAuthorName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}