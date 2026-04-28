package com.ildang100.backoffice.common.enums;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;

/**
 * 상품 상태를 정의하는 Enum입니다.
 *
 * <p>
 * 상품의 판매 가능 여부, 품절 여부, 단종 여부를 판단하기 위해 사용됩니다.
 * Spring Security는 API 접근 권한을 담당하고,
 * 상품 상태에 따른 비즈니스 조건 검증은 해당 Enum이 담당합니다.
 * </p>
 *
 * <p>
 * 즉, "누가 상품을 수정/삭제할 수 있는가"는 Spring Security에서 처리하고,
 * "현재 상품 상태에서 판매/노출/재입고가 가능한가"는 이 Enum에서 처리합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-25
 */
public enum ProductStatus {

    /**
     * 판매중 상태입니다.
     * 상품 구매 및 노출이 가능합니다.
     */
    ON_SALE("판매중"),

    /**
     * 품절 상태입니다.
     * 상품 노출은 가능하지만 구매는 제한됩니다.
     */
    OUT_OF_STOCK("품절"),

    /**
     * 단종 상태입니다.
     * 일반적으로 상품 노출 및 판매가 제한됩니다.
     */
    DISCONTINUED("단종");

    /**
     * 화면 표시 또는 응답 DTO에서 사용할 상태 설명입니다.
     */
    private final String description;

    /**
     * 상품 상태 Enum 생성자입니다.
     *
     * @param description 상태 설명
     */
    ProductStatus(String description) {
        this.description = description;
    }

    /**
     * 문자열 값을 {@link ProductStatus}로 변환합니다.
     *
     * @param value 변환할 상품 상태 문자열
     * @return 변환된 ProductStatus
     * @throws ServiceException 유효하지 않은 상품 상태 값인 경우
     */
    public static ProductStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_PRODUCT_STATUS);
        }

        try {
            return ProductStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ErrorCode.INVALID_PRODUCT_STATUS);
        }
    }

    /**
     * 상품 판매 가능 상태인지 검증합니다.
     *
     * <p>
     * API 접근 권한은 Spring Security에서 검증하고,
     * 해당 메서드는 상품 상태상 판매가 가능한지만 검증합니다.
     * </p>
     *
     * @throws ServiceException 판매할 수 없는 상품 상태인 경우
     */
    public void validateSellable() {
        if (this != ON_SALE) {
            throw new ServiceException(ErrorCode.INVALID_PRODUCT_STATUS);
        }
    }

    /**
     * 상품 판매 가능 여부를 반환합니다.
     *
     * @return ON_SALE 상태이면 true
     */
    public boolean canSell() {
        return this == ON_SALE;
    }

    /**
     * 상품 노출 가능 여부를 반환합니다.
     *
     * @return ON_SALE 또는 OUT_OF_STOCK 상태이면 true
     */
    public boolean canDisplay() {
        return this == ON_SALE || this == OUT_OF_STOCK;
    }

    /**
     * 재입고 가능 여부를 반환합니다.
     *
     * @return OUT_OF_STOCK 상태이면 true
     */
    public boolean canRestock() {
        return this == OUT_OF_STOCK;
    }

    /**
     * 상태 설명을 반환합니다.
     *
     * @return 상태 설명
     */
    public String getDescription() {
        return description;
    }
}