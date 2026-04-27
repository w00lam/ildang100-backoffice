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
 * <p><b>설계 원칙</b></p>
 * <ul>
 *     <li>생성은 팩토리 메서드({@link #create})를 통해서만 가능합니다.</li>
 *     <li>등록 관리자(admin)는 생성 시점에 결정되며 이후 변경되지 않습니다.</li>
 *     <li>비즈니스 예외는 모두 {@link ServiceException} 형태로 발생시킵니다.</li>
 * </ul>
 *
 * <p><b>생성 시 status 결정 규칙</b></p>
 * <ul>
 *     <li>요청 상태가 {@link ProductStatus#DISCONTINUED} → 그대로 DISCONTINUED 등록
 *         (운영자의 명시적 단종 등록 허용)</li>
 *     <li>그 외 → stock 기반 자동 결정
 *         (stock {@literal <=} 0 → OUT_OF_STOCK, stock {@literal >=} 1 → ON_SALE)</li>
 * </ul>
 *
 * <p>
 * 본 규칙으로 "stock=0 + status=ON_SALE" 같은 정합성 위반 등록을 원천 차단합니다.
 * 정보 수정은 후속 PR에서 {@code updateInfo} 메서드로,
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false, updatable = false)
    private Admin admin;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 30, nullable = false)
    private String category;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
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
     * 상품 엔티티 생성 팩토리 메서드입니다.
     *
     * <p>
     * 입력값 검증 후 "생성 시 status 결정 규칙"에 따라 status를 자동으로 결정합니다.
     * name과 category는 trim 처리 후 저장되어 좌우 공백을 제거합니다.
     * validation 자체는 도메인 서비스 전용 validation입니다.
     * </p>
     *
     * @param admin           등록 관리자 (필수, 생성 후 변경 불가)
     * @param name            상품명 (1~30자, 공백만 허용 안 됨)
     * @param category        카테고리 (1~30자, 공백만 허용 안 됨)
     * @param price           판매 가격 (0 이상 정수)
     * @param stock           재고 수량 (0 이상 정수)
     * @param requestedStatus 요청된 판매 상태
     *                        (DISCONTINUED 외에는 stock 기반 자동 결정에 의해 무시됨)
     * @return 생성된 Product 엔티티
     * @throws ServiceException 입력값 검증 실패 시
     *                          ({@link ErrorCode#VALIDATION_FAILED},
     *                          {@link ErrorCode#INVALID_STOCK_VALUE},
     *                          {@link ErrorCode#INVALID_PRODUCT_STATUS})
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
        validateName(name);
        validateCategory(category);
        validatePrice(price);
        validateStock(stock);
        validateRequestedStatus(requestedStatus);

        ProductStatus resolvedStatus = resolveStatusOnCreate(stock, requestedStatus);

        return new Product(admin, name.trim(), category.trim(), price, stock, resolvedStatus);
    }

    private static void validateAdmin(Admin admin) {
        if (admin == null) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        if (name.trim().length() > NAME_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }

    private static void validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        if (category.trim().length() > CATEGORY_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
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

    private static ProductStatus resolveStatusOnCreate(int stock, ProductStatus requestedStatus) {
        if (requestedStatus == ProductStatus.DISCONTINUED) {
            return ProductStatus.DISCONTINUED;
        }
        return stock <= 0 ? ProductStatus.OUT_OF_STOCK : ProductStatus.ON_SALE;
    }
}