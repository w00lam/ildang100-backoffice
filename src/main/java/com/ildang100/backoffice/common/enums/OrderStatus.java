package com.ildang100.backoffice.common.enums;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;

/**
 * 주문 상태를 정의하는 Enum입니다.
 *
 * <p>
 * 주문 처리 흐름, 취소 가능 여부, 상태 변경 가능 여부를 판단하기 위해 사용됩니다.
 * Spring Security는 API 접근 권한을 담당하고,
 * 주문 상태 전이 검증은 해당 Enum이 담당합니다.
 * </p>
 *
 * <p>
 * 즉, "누가 이 API를 호출할 수 있는가"는 Spring Security에서 처리하고,
 * "현재 주문 상태에서 이 작업이 가능한가"는 이 Enum에서 처리합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-25
 */
public enum OrderStatus {

    /**
     * 상품 준비중 상태입니다.
     * 주문 취소가 가능한 상태입니다.
     */
    PREPARING("준비중"),

    /**
     * 배송중 상태입니다.
     * 일반적으로 주문 취소가 제한됩니다.
     */
    SHIPPING("배송중"),

    /**
     * 배송 완료 상태입니다.
     * 종료 상태로 간주됩니다.
     */
    DELIVERED("배송완료"),

    /**
     * 주문 취소 상태입니다.
     * 종료 상태로 간주됩니다.
     */
    CANCELLED("취소");

    /**
     * 화면 표시 또는 응답 DTO에서 사용할 상태 설명입니다.
     */
    private final String description;

    /**
     * 주문 상태 Enum 생성자입니다.
     *
     * @param description 상태 설명
     */
    OrderStatus(String description) {
        this.description = description;
    }

    /**
     * 문자열 값을 {@link OrderStatus}로 변환합니다.
     *
     * @param value 변환할 주문 상태 문자열
     * @return 변환된 OrderStatus
     * @throws ServiceException 유효하지 않은 주문 상태 값인 경우
     */
    public static OrderStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }

        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }

    /**
     * 주문 취소 가능 상태인지 검증합니다.
     *
     * <p>
     * API 접근 권한은 Spring Security에서 먼저 검증하고,
     * 해당 메서드는 도메인 상태상 취소가 가능한지만 검증합니다.
     * </p>
     *
     * @throws ServiceException 주문 취소가 불가능한 상태인 경우
     */
    public void validateCancelable() {
        if (this != PREPARING) {
            throw new ServiceException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
        }
    }

    /**
     * 주문 상태 변경 가능 여부를 검증합니다.
     *
     * <p>
     * 배송 완료 또는 취소 상태는 종료 상태로 간주하므로,
     * 추가적인 상태 변경을 허용하지 않습니다.
     * </p>
     *
     * @param next 변경하려는 다음 주문 상태
     * @throws ServiceException 허용되지 않는 상태 변경인 경우
     */
    public void validateTransition(OrderStatus next) {
        if (next == null) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }

        if (this == DELIVERED || this == CANCELLED) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }

    /**
     * 주문 취소 가능 여부를 반환합니다.
     *
     * @return PREPARING 상태이면 true
     */
    public boolean canCancel() {
        return this == PREPARING;
    }

    /**
     * 주문 종료 상태 여부를 반환합니다.
     *
     * @return DELIVERED 또는 CANCELLED 상태이면 true
     */
    public boolean isFinished() {
        return this == DELIVERED || this == CANCELLED;
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