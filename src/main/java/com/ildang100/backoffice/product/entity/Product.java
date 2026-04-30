package com.ildang100.backoffice.product.entity;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.DeletionStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeletionStatus deletionStatus;

    private Product(Admin admin, String name, String category, int price, int stock, ProductStatus status) {
        this.admin = admin;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.deletionStatus = DeletionStatus.NOT_DELETED;
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

    /**
     * 상품 재고를 절대값으로 설정합니다 (운영자 채널 / P-4).
     *
     * <p>
     * 변경 후 자동 전이 규칙(§{@link #applyStockChange})에 따라 상태가 갱신됩니다.
     * 단종 상품은 재고만 변경되고 상태는 단종으로 유지됩니다.
     * </p>
     *
     * @param newStock 새 재고 수량 (0 이상)
     * @throws ServiceException newStock이 음수인 경우 ({@link ErrorCode#INVALID_STOCK_VALUE})
     */
    public void changeStock(int newStock) {
        validateStock(newStock);
        applyStockChange(newStock);
    }

    /**
     * 상품 재고를 차감합니다 (주문 도메인 채널 / P-4·P-7).
     *
     * <p>
     * 차감 후 자동 전이 규칙에 따라 상태가 갱신됩니다.
     * 단종/품절 상품에 대한 주문 가능 여부 검증은 호출자(Order Service) 책임입니다 — 도메인 메서드는
     * 재고/상태 정합성만 보장합니다.
     * </p>
     *
     * @param quantity 차감 수량 (1 이상)
     * @throws ServiceException quantity가 1 미만 ({@link ErrorCode#INVALID_QUANTITY})
     *                          또는 현재 재고보다 큰 경우 ({@link ErrorCode#INSUFFICIENT_STOCK})
     */
    public void decreaseStock(int quantity) {
        validateQuantity(quantity);
        if (this.stock < quantity) {
            throw new ServiceException(ErrorCode.INSUFFICIENT_STOCK);
        }
        applyStockChange(this.stock - quantity);
    }

    /**
     * 상품 재고를 복구합니다 (주문 취소 채널 / P-4·P-7).
     *
     * <p>
     * 복구 후 자동 전이 규칙에 따라 상태가 갱신됩니다.
     * 품절 상품이 복구되어 재고가 1 이상이 되면 자동으로 판매중으로 전이되고,
     * 단종 상품은 재고만 변경되고 상태는 유지됩니다.
     * </p>
     *
     * @param quantity 복구 수량 (1 이상)
     * @throws ServiceException quantity가 1 미만인 경우 ({@link ErrorCode#INVALID_QUANTITY})
     */
    public void restoreStock(int quantity) {
        validateQuantity(quantity);
        applyStockChange(this.stock + quantity);
    }

    /**
     * 재고 변경 + 상태 자동 전이 (Aggregate 내부 캡슐화).
     *
     * <p>
     * 단종(DISCONTINUED) 상품은 재고만 변경되고 상태는 유지됩니다.
     * 그 외 상태에서는 stock 기반으로 상태가 자동 결정됩니다 (stock ≤ 0 → OUT_OF_STOCK,
     * stock ≥ 1 → ON_SALE).
     * </p>
     */
    private void applyStockChange(int newStock) {
        this.stock = newStock;
        if (this.status == ProductStatus.DISCONTINUED) {
            return; // 단종은 자동 전이 대상이 아님
        }
        this.status = (newStock <= 0) ? ProductStatus.OUT_OF_STOCK : ProductStatus.ON_SALE;
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new ServiceException(ErrorCode.INVALID_QUANTITY);
        }
    }

    /**
     * 상품 상태를 명시적으로 변경합니다 (운영자 의사결정 채널 / P-5).
     *
     * <p>
     * 자동 전이 정책({@link #applyStockChange})과 독립적으로 동작합니다.
     * 재고와 무관하게 어떤 상태로든 변경 가능하며, 본 메서드로 {@code DISCONTINUED}로
     * 설정된 상품은 이후 재고 변경 시에도 자동 전이 대상에서 제외되어 단종으로 유지됩니다.
     * </p>
     *
     * @param newStatus 새 판매 상태 (null 불가)
     * @throws ServiceException newStatus가 null인 경우 ({@link ErrorCode#INVALID_PRODUCT_STATUS})
     */
    public void changeStatus(ProductStatus newStatus) {
        if (newStatus == null) {
            throw new ServiceException(ErrorCode.INVALID_PRODUCT_STATUS);
        }
        this.status = newStatus;
    }

    /**
     * 상품을 소프트 삭제 처리합니다 (운영자 채널 / P-6).
     *
     * <p>
     * 데이터는 물리적으로 삭제되지 않으며, {@code deletionStatus}만 {@code DELETED}로 전이됩니다.
     * 상품의 {@code status}(판매 상태)는 변경되지 않고 그대로 보존되며, 연결된 주문/리뷰는
     * 그대로 유지되어 이력·통계에 영향을 주지 않습니다.
     * </p>
     *
     * @throws ServiceException 이미 {@code DELETED} 상태인 경우
     *                          ({@link ErrorCode#PRODUCT_ALREADY_DELETED})
     */
    public void markAsDeleted() {
        if (this.deletionStatus == DeletionStatus.DELETED) {
            throw new ServiceException(ErrorCode.PRODUCT_ALREADY_DELETED);
        }
        this.deletionStatus = DeletionStatus.DELETED;
    }

    /**
     * 상품이 소프트 삭제된 상태인지 여부를 반환합니다.
     */
    public boolean isDeleted() {
        return this.deletionStatus == DeletionStatus.DELETED;
    }

    /**
     * 주문 가능 여부 사전 검증 (P-7).
     *
     * <p>
     * Order 도메인이 본 Aggregate를 대상으로 주문을 생성하기 직전에 호출하는 검증 진입점.
     * 다음 두 케이스를 차단합니다:
     * <ul>
     *   <li>{@code DeletionStatus.DELETED} — 도메인 정책상 "조회되지 않는 것으로 취급" →
     *       {@link ErrorCode#PRODUCT_NOT_FOUND}(404)</li>
     *   <li>{@code ProductStatus.DISCONTINUED} — 영구 단종 →
     *       {@link ErrorCode#PRODUCT_DISCONTINUED}(409)</li>
     * </ul>
     *
     * <p>
     * 재고 부족({@code stock < quantity})은 본 메서드가 아니라 후행 호출인
     * {@link #decreaseStock(int)}에서 {@link ErrorCode#INSUFFICIENT_STOCK}으로 차단됩니다 —
     * 검증 중복을 피하기 위함.
     * </p>
     *
     * @throws ServiceException 주문할 수 없는 상품인 경우
     */
    public void assertOrderable() {
        if (this.deletionStatus == DeletionStatus.DELETED) {
            throw new ServiceException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (this.status == ProductStatus.DISCONTINUED) {
            throw new ServiceException(ErrorCode.PRODUCT_DISCONTINUED);
        }
    }

}
