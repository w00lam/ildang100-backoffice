package com.ildang100.backoffice.customer.dto;

import lombok.Getter;

/**
 * 고객별 주문 통계 정보를 표현하는 DTO입니다.
 *
 * <p>취소 주문을 제외한 총 주문 건수와 총 주문 금액을 담습니다.</p>
 */
@Getter
public class CustomerOrderStats {

    private final Long customerId;
    private final long totalOrderCount;
    private final long totalOrderAmount;

    public CustomerOrderStats(
            Long customerId,
            Long totalOrderCount,
            Long totalOrderAmount
    ) {
        this.customerId = customerId;
        this.totalOrderCount = totalOrderCount == null ? 0 : totalOrderCount;
        this.totalOrderAmount = totalOrderAmount == null ? 0 : totalOrderAmount;
    }

    /**
     * 주문 통계가 없는 고객의 기본 통계 값을 생성합니다.
     *
     * @param customerId 고객 ID
     * @return 주문 건수와 주문 금액이 0인 고객 주문 통계
     */
    public static CustomerOrderStats empty(Long customerId) {
        return new CustomerOrderStats(customerId, 0L, 0L);
    }
}
