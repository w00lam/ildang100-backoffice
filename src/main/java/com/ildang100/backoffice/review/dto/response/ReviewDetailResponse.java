package com.ildang100.backoffice.review.dto.response;

import com.ildang100.backoffice.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 리뷰 상세 조회 응답 DTO (Story R-2).
 *
 * <p>
 * 목록 응답({@link ReviewListItemResponse})과 달리 작성 고객의 이메일(customerEmail)을
 * 추가로 노출합니다 — 운영자의 고객 응대 채널 식별 용도. 반대로 orderNumber는 제외하며,
 * 주문 추적은 별도 주문 메뉴에서 수행됩니다.
 * </p>
 *
 * @author [작성자]
 * @since 2026-04-29
 */
@Getter
public class ReviewDetailResponse {

    private final Long id;
    private final String productName;
    private final String customerName;
    private final String customerEmail;
    private final int rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ReviewDetailResponse(
            Long id,
            String productName,
            String customerName,
            String customerEmail,
            int rating,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
                                ) {
        this.id = id;
        this.productName = productName;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Review 엔티티로부터 상세 응답 DTO를 생성합니다.
     *
     * <p>
     * 호출 시점에 {@code review.customer} / {@code review.product}가 fetch join으로
     * 함께 로딩되어 있어야 합니다 — LAZY proxy 상태로 호출하면 N+1이 발생합니다
     * ({@link com.ildang100.backoffice.review.repository.ReviewRepository#findDetailByProductIdAndId}).
     * </p>
     */
    public static ReviewDetailResponse from(Review review) {
        return new ReviewDetailResponse(
                review.getId(),
                review.getProduct().getName(),
                review.getCustomer().getName(),
                review.getCustomer().getEmail(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
