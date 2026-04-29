package com.ildang100.backoffice.product.dto.response;

import com.ildang100.backoffice.common.enums.ProductStatus;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.review.dto.response.ProductReviewSummary;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 상세 응답 DTO입니다.
 *
 * <p>
 * 상품 단건 상세 조회(GET /admin/products/{productId}) 응답에 사용됩니다.
 * 통합 응답 DTO인 {@link ProductResponse}의 필드에 더해, 등록 관리자의
 * 이메일({@code adminEmail})을 포함하여 운영 관리자가 상품을 검토할 때
 * 등록자의 연락처까지 한 번에 확인할 수 있게 합니다.
 * </p>
 *
 * <p>
 * 리뷰 통계 연동(평균 평점, 리뷰 개수, 최신 리뷰 3건)은 Story P-8에서 추가 예정입니다.
 * </p>
 *
 * <p>
 * 외부 직접 생성을 막고 {@link #from(Product)} 팩토리 메서드를 통해서만 생성됩니다.
 * </p>
 *
 * @author js-kim-arc
 * @since 2026-04-28
 */
@Getter
public class ProductDetailResponse {

    private final Long id;
    private final String adminName;
    private final String adminEmail;
    private final String name;
    private final String category;
    private final int price;
    private final int stock;
    private final ProductStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final ProductReviewSummary reviewSummary;

    private ProductDetailResponse(
            Long id,
            String adminName,
            String adminEmail,
            String name,
            String category,
            int price,
            int stock,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            ProductReviewSummary reviewSummary
                                 ) {
        this.id = id;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewSummary = reviewSummary;
    }

    /**
     * 상품 본체와 리뷰 통계를 합성하여 상세 응답을 생성합니다.
     *
     * <p>
     * 호출자({@code ProductService#getDetail}) 책임:
     * <ul>
     *     <li>{@code product}는 fetch join으로 {@code admin}이 함께 로딩된 상태여야 함
     *         — {@code adminName}/{@code adminEmail} 호출 시 N+1 회피</li>
     *     <li>{@code reviewSummary}는 항상 non-null. 리뷰 0건이면
     *         {@link ProductReviewSummary#empty()}로 전달 (필드 자체는 항상 존재)</li>
     * </ul>
     * </p>
     */
    public static ProductDetailResponse from(Product product, ProductReviewSummary reviewSummary) {
        return new ProductDetailResponse(
                product.getId(),
                product.getAdmin().getName(),
                product.getAdmin().getEmail(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                reviewSummary
        );
    }
}
