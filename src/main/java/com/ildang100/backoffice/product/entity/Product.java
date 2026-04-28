package com.ildang100.backoffice.product.entity;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.ProductStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 정보를 나타내는 엔티티(Aggregate Root)입니다.
 *
 * <p>
 * 판매 카탈로그의 기본 단위로서, 상품의 기본 정보(name, category, price)와
 * 운영 데이터(stock, status)를 하나의 단위로 묶어 일관성을 보장합니다.
 * JPA를 통해 DB의 products 테이블과 매핑됩니다.
 * </p>
 *
 * <p>
 * 본 규칙으로 "stock=0 + status=ON_SALE" 같은 정합성 위반 등록을 원천 차단합니다.
 * 재고 변경에 따른 상태 자동 전이는 Story P-4에서 {@code changeStock} 메서드로 추가됩니다.
 * </p>
 *
 * @author js-kim-arc
 * @since 2026-04-27
 */
@Entity
@Getter
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    private static final int NAME_MAX_LENGTH = 30;
    private static final int CATEGORY_MAX_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    private Product(Admin admin, String name, String category, int price, int stock, ProductStatus status) {
        this.admin = admin;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    /**
     * 상품 생성 팩토리.
     * 생성 시 status는 stock과 requestedStatus 조합으로 자동 결정된다.
     * - requestedStatus == DISCONTINUED → DISCONTINUED 유지
     * - 그 외: stock <= 0 → OUT_OF_STOCK, stock >= 1 → ON_SALE
     */
    public static Product create(
            Admin admin,
            String name,
            String category,
            int price,
            int stock,
            ProductStatus requestedStatus
                                ) {
        validateAdmin(admin);
        String trimmedName = validateAndTrimName(name);
        String trimmedCategory = validateAndTrimCategory(category);
        validatePrice(price);
        validateStock(stock);
        validateRequestedStatus(requestedStatus);

        ProductStatus resolvedStatus = resolveCreationStatus(stock, requestedStatus);

        return new Product(admin, trimmedName, trimmedCategory, price, stock, resolvedStatus);
    }

    /**
     * 상품 기본 정보 부분 수정. - update
     * null로 전달된 필드는 기존 값 유지 (부분 수정 의미론).
     * 모든 필드가 null이면 early return — 불필요한 dirty checking 회피 + 멱등성 보장.
     * stock과 status는 변경하지 않는다 (분리된 채널).
     */
    public void updateInfo(String name, String category, Integer price) {
        // 모든 필드가 null이면 변경 없이 종료 (API 명세서: 멱등성 보장)
        if (name == null && category == null && price == null) {
            return;
        }

        if (name != null) {
            this.name = validateAndTrimName(name);
        }
        if (category != null) {
            this.category = validateAndTrimCategory(category);
        }
        if (price != null) {
            validatePrice(price);
            this.price = price;
        }
    }

    // ---- 검증 헬퍼  ----

    private static void validateAdmin(Admin admin) {
        if (admin == null) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }

    private static String validateAndTrimName(String name) {
        if (name == null || name.isBlank()) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        String trimmed = name.trim();
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        return trimmed;
    }

    private static String validateAndTrimCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        String trimmed = category.trim();
        if (trimmed.length() > CATEGORY_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        return trimmed;
    }

    private static void validatePrice(int price) {
        if (price < 0) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }

    private static void validateStock(int stock) {
        if (stock < 0) {
            throw new ServiceException(ErrorCode.INVALID_STOCK_VALUE);
        }
    }

    private static void validateRequestedStatus(ProductStatus requestedStatus) {
        if (requestedStatus == null) {
            throw new ServiceException(ErrorCode.INVALID_PRODUCT_STATUS);
        }
    }

    private static ProductStatus resolveCreationStatus(int stock, ProductStatus requestedStatus) {
        if (requestedStatus == ProductStatus.DISCONTINUED) {
            return ProductStatus.DISCONTINUED;
        }
        return (stock <= 0) ? ProductStatus.OUT_OF_STOCK : ProductStatus.ON_SALE;
    }
}