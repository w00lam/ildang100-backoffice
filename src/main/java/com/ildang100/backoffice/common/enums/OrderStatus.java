package com.ildang100.backoffice.common.enums;

/**
 * 주문 상태를 정의하는 Enum입니다.
 *
 * <p>주문의 처리 진행 상태를 나타냅니다.</p>
 *
 * <ul>
 *     <li>PREPARED - 주문 준비 완료 상태</li>
 *     <li>SHIPPING - 배송 중 상태</li>
 *     <li>DELIVERED - 배송 완료 상태</li>
 *     <li>CANCELED - 주문 취소 상태</li>
 * </ul>
 */
public enum OrderStatus {
    PREPARING,
    SHIPPING,
    DELIVERED,
    CANCELLED
}
