package com.ildang100.backoffice.common.enums;

/**
 * 상품 상태를 정의하는 Enum입니다.
 *
 * <p>상품의 판매 가능 여부 및 상태를 나타냅니다.</p>
 *
 * <ul>
 *     <li>ON_SALE - 판매 중인 상품</li>
 *     <li>OUT_OF_STOCK - 재고가 없는 상태</li>
 *     <li>DISCONTINUED - 판매 종료된 상품</li>
 * </ul>
 */
public enum ProductStatus {
    ON_SALE,
    OUT_OF_STOCK,
    DISCONTINUED
}
