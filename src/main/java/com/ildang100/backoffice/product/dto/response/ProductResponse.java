package com.ildang100.backoffice.product.dto.response;

import com.ildang100.backoffice.common.enums.ProductStatus;
import com.ildang100.backoffice.product.entity.Product;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 응답 DTO입니다.
 *
 * <p>
 * 상품 도메인의 CRUD 응답 전반에서 공통으로 사용되는 통합 응답 객체입니다.
 * 등록(POST), 단건 조회(GET), 정보 수정(PUT), 목록 조회의 각 항목으로 사용됩니다.
 * </p>
 *
 * <p>
 * 도메인 정책에 따라 자동 보정된 {@code status} 값이 응답에 그대로 반영됩니다.
 * (예: {@code stock=0} + {@code status=ON_SALE} 등록 요청 시 응답의 status는 {@code OUT_OF_STOCK})
 * </p>
 *
 * <p>
 * 등록 관리자 정보는 ID 대신 이름({@code adminName})을 노출하여
 * 다른 조회 API와의 일관성을 유지합니다.
 * </p>
 *
 * <p>
 * 외부 직접 생성을 막고 {@link #from(Product)} 팩토리 메서드를 통해서만 생성됩니다.
 * 향후 목록 조회(Story P-2)에서 페이로드 경량화가 필요해지면
 * {@code ProductSummaryResponse} 분리를 검토합니다.
 * </p>
 *
 * @author js-kim-arc
 * @since 2026-04-27
 */
@Getter
public class ProductResponse {

    private final Long id;
    private final String adminName;
    private final String name;
    private final String category;
    private final int price;
    private final int stock;
    private final ProductStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ProductResponse(
            Long id,
            String adminName,
            String name,
            String category,
            int price,
            int stock,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
                           ) {
        this.id = id;
        this.adminName = adminName;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getAdmin().getName(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}