package com.ildang100.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ildang100.backoffice.common.enums.OrderStatus;
import lombok.Getter;

/**
 * 대시보드 최근 주문 응답 DTO입니다.
 *
 * <p>
 * 관리자 대시보드에서 최근 주문 목록을 표시하기 위해 사용됩니다.
 * 주문번호, 고객명, 상품명, 주문 금액, 주문 상태를 포함합니다.
 * </p>
 *
 * <p>
 * 해당 객체는 불변(immutable) 객체로 설계되었으며,
 * 생성은 정적 팩토리 메서드를 통해서만 가능합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
@JsonPropertyOrder({
        "orderNumber",
        "customerName",
        "productName",
        "amount",
        "status"
})
public class RecentOrderResponse {

    private final Long orderNumber;
    private final String customerName;
    private final String productName;
    private final Integer amount;
    private final OrderStatus status;

    public RecentOrderResponse(
            Long orderNumber,
            String customerName,
            String productName,
            Integer totalPrice,
            OrderStatus status
    ) {
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.productName = productName;
        this.amount = totalPrice;
        this.status = status;
    }

    /**
     * 최근 주문 응답 객체를 생성하는 정적 팩토리 메서드입니다.
     *
     * <p>
     * JPQL constructor expression을 사용하지 않고,
     * Service 계층에서 명시적으로 DTO를 생성할 때 사용합니다.
     * </p>
     *
     * @param orderNumber  주문번호
     * @param customerName 고객명
     * @param productName  상품명
     * @param amount       주문 금액
     * @param status       주문 상태
     * @return RecentOrderResponse 생성된 DTO 객체
     */
    public static RecentOrderResponse of(
            Long orderNumber,
            String customerName,
            String productName,
            Integer amount,
            OrderStatus status
    ) {
        return new RecentOrderResponse(
                orderNumber,
                customerName,
                productName,
                amount,
                status
        );
    }
}
