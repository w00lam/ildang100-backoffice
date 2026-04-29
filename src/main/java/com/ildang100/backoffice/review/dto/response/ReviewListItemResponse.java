package com.ildang100.backoffice.review.dto.response;

import com.ildang100.backoffice.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 리뷰 목록 조회 응답 항목 DTO (Story R-1).
 *
 * @author [작성자]
 * @since 2026-04-29
 */
@Getter
public class ReviewListItemResponse {

    private final Long id;
    private final Long orderNumber;
    private final String customerName;
    private final String productName;
    private final int rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ReviewListItemResponse(
            Long id,
            Long orderNumber,
            String customerName,
            String productName,
            int rating,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
                                  ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.productName = productName;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Review 엔티티로부터 응답 DTO를 생성한다.
     *
     * <p>
     * {@code orderNumber}는 현재 {@code Order#getId()}로 매핑한다.
     * Order 도메인이 별도의 비즈니스 식별자(예: {@code orderNumber} 컬럼)를 도입하면
     * 그 시점에 매핑 변경 (Order 도메인 담당자와 합의 필요).
     * </p>
     */
    public static ReviewListItemResponse from(Review review) {
        return new ReviewListItemResponse(
                review.getId(),
                review.getOrder().getOrderNumber(),
                review.getCustomer().getName(),
                review.getProduct().getName(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
