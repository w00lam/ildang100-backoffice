package com.ildang100.backoffice.review.entity;

import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.DeletionStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.order.entity.Order;
import com.ildang100.backoffice.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 고객이 작성한 상품 리뷰를 나타내는 엔티티(Aggregate Root)입니다.
 *
 * <p>
 * 고객·상품·주문을 모두 참조하는 도메인으로, 상품 카탈로그 품질 지표
 * (평균 평점·별점별 분포·최신 리뷰)의 단일 진실원천 역할을 합니다.
 * 작성 정보(rating, content)와 운영 상태(deletionStatus)를 분리해서 가지며,
 * 삭제된 리뷰는 모든 조회·통계 집계에서 제외됩니다.
 * </p>

 *
 * @author [작성자]
 * @since 2026-04-29
 */
@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    private static final int CONTENT_MAX_LENGTH = 500;
    private static final int RATING_MIN = 1;
    private static final int RATING_MAX = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = CONTENT_MAX_LENGTH)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeletionStatus deletionStatus;

    private Review(Customer customer, Product product, Order order, int rating, String content) {
        this.customer = customer;
        this.product = product;
        this.order = order;
        this.rating = rating;
        this.content = content;
        this.deletionStatus = DeletionStatus.NOT_DELETED;
    }

    /**
     * 리뷰 생성 팩토리 (고객 채널 / v2).
     *
     * <p>
     * 본 epic 범위에서는 어드민 채널이 호출하지 않으며, 고객 채널 API(v2)에서 호출됩니다.
     * Aggregate 정의의 일부로 포함되어 v2 작업 시 즉시 사용 가능하도록 미리 잠가둡니다.
     * </p>
     *
     * @throws ServiceException customer/product/order 누락 ({@link ErrorCode#VALIDATION_FAILED}),
     *                          rating 1~5 범위 밖 ({@link ErrorCode#INVALID_RATING_VALUE}),
     *                          content 누락/blank/500자 초과 ({@link ErrorCode#VALIDATION_FAILED})
     */
    public static Review create(
            Customer customer,
            Product product,
            Order order,
            int rating,
            String content
                               ) {
        validateAssociations(customer, product, order);
        validateRating(rating);
        String trimmedContent = validateAndTrimContent(content);
        return new Review(customer, product, order, rating, trimmedContent);
    }

    /**
     * 리뷰를 소프트 삭제 처리합니다 (운영자 채널 / R-3).
     *
     * <p>
     * 데이터는 물리적으로 삭제되지 않으며, {@code deletionStatus}만 {@code DELETED}로 전이됩니다.
     * 작성 정보(rating·content·createdAt 등)는 변경되지 않고 그대로 보존됩니다 —
     * 어뷰징 추적 및 통계 보존 목적.
     * </p>
     *
     * @throws ServiceException 이미 {@code DELETED} 상태인 경우
     *                          ({@link ErrorCode#REVIEW_ALREADY_DELETED})
     */
    public void markAsDeleted() {
        if (this.deletionStatus == DeletionStatus.DELETED) {
            throw new ServiceException(ErrorCode.REVIEW_ALREADY_DELETED);
        }
        this.deletionStatus = DeletionStatus.DELETED;
    }

    /**
     * 리뷰가 소프트 삭제된 상태인지 여부를 반환합니다.
     */
    public boolean isDeleted() {
        return this.deletionStatus == DeletionStatus.DELETED;
    }

    // ---- 검증 헬퍼 ----

    private static void validateAssociations(Customer customer, Product product, Order order) {
        if (customer == null || product == null || order == null) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }

    private static void validateRating(int rating) {
        if (rating < RATING_MIN || rating > RATING_MAX) {
            throw new ServiceException(ErrorCode.INVALID_RATING_VALUE);
        }
    }

    private static String validateAndTrimContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        String trimmed = content.trim();
        if (trimmed.length() > CONTENT_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        return trimmed;
    }
}