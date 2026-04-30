package com.ildang100.backoffice.review.dto.response;

import com.ildang100.backoffice.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LatestReviewItem {

    private final Long id;
    private final String customerName;
    private final int rating;
    private final String content;
    private final LocalDateTime createdAt;

    private LatestReviewItem(
            Long id,
            String customerName,
            int rating,
            String content,
            LocalDateTime createdAt
                            ) {
        this.id = id;
        this.customerName = customerName;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static LatestReviewItem from(Review review) {
        return new LatestReviewItem(
                review.getId(),
                review.getCustomer().getName(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt()
        );
    }
}
