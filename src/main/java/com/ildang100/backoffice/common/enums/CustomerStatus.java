package com.ildang100.backoffice.common.enums;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;

/**
 * 고객 계정 상태를 정의하는 Enum입니다.
 *
 * <p>
 * 고객 계정의 서비스 이용 가능 여부를 판단하기 위해 사용됩니다.
 * 이후 Spring Security 적용 시 관리자 인증/인가와 별개로,
 * 비즈니스 로직에서 고객 상태 검증 조건으로 사용할 수 있습니다.
 * </p>
 *
 * <p>
 * 고객이 직접 로그인하는 구조가 추가될 경우,
 * 해당 상태값은 Security 인증 전 계정 상태 검증에도 활용될 수 있습니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-25
 */
public enum CustomerStatus {

    /**
     * 활성 상태입니다.
     * 서비스 이용이 가능합니다.
     */
    ACTIVE("활성"),

    /**
     * 비활성 상태입니다.
     * 서비스 이용이 제한됩니다.
     */
    INACTIVE("비활성"),

    /**
     * 정지 상태입니다.
     * 정책 위반 등으로 인해 서비스 이용이 제한됩니다.
     */
    SUSPENDED("정지");

    /**
     * 화면 표시 또는 응답 DTO에서 사용할 상태 설명입니다.
     */
    private final String description;

    /**
     * 고객 상태 Enum 생성자입니다.
     *
     * @param description 상태 설명
     */
    CustomerStatus(String description) {
        this.description = description;
    }

    /**
     * 문자열 값을 {@link CustomerStatus}로 변환합니다.
     *
     * @param value 변환할 고객 상태 문자열
     * @return 변환된 CustomerStatus
     * @throws ServiceException 유효하지 않은 고객 상태 값인 경우
     */
    public static CustomerStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_CUSTOMER_STATUS);
        }

        try {
            return CustomerStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ErrorCode.INVALID_CUSTOMER_STATUS);
        }
    }

    /**
     * 고객이 서비스를 이용할 수 있는 상태인지 검증합니다.
     *
     * <p>
     * 현재는 비즈니스 로직에서 사용하는 상태 검증 메서드입니다.
     * 추후 고객 인증이 Spring Security로 확장될 경우,
     * 인증 전 계정 상태 검증에도 사용할 수 있습니다.
     * </p>
     *
     * @throws ServiceException 서비스 이용이 제한된 고객 상태인 경우
     */
    public void validateUsable() {
        if (this != ACTIVE) {
            throw new ServiceException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 서비스 이용 가능 여부를 반환합니다.
     *
     * @return ACTIVE 상태이면 true
     */
    public boolean canUseService() {
        return this == ACTIVE;
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