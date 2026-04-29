package com.ildang100.backoffice.common.enums;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;

/**
 * 관리자 계정 상태를 정의하는 Enum입니다.
 *
 * <p>
 * 관리자 계정의 로그인 가능 여부, 승인 상태, 정지 상태 등을 구분합니다.
 * 이후 Spring Security 기반 JWT 인증을 적용할 예정이며,
 * 로그인 과정에서 계정 상태 검증 후 인증 객체를 생성하는 방식으로 사용됩니다.
 * </p>
 *
 * <p>
 * 즉, Security 필터 또는 로그인 서비스에서 관리자 상태가 {@code ACTIVE}인지 검증한 뒤,
 * 인증 성공 시 SecurityContext에 인증 정보를 저장하는 구조로 확장할 수 있습니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-25
 */
public enum AdminStatus {

    /**
     * 활성 상태입니다.
     * 로그인 및 서비스 이용이 가능합니다.
     */
    ACTIVE("활성"),

    /**
     * 비활성 상태입니다.
     * 로그인이 제한됩니다.
     */
    INACTIVE("비활성"),

    /**
     * 정지 상태입니다.
     * 정책 위반 등으로 인해 로그인이 제한됩니다.
     */
    SUSPENDED("정지"),

    /**
     * 승인 대기 상태입니다.
     * 관리자 가입 요청 후 승인 전 상태입니다.
     */
    PENDING_APPROVAL("승인 대기"),

    /**
     * 승인 거절 상태입니다.
     * 관리자 가입 요청이 거절된 상태입니다.
     */
    REJECTED("거절");

    /**
     * 화면 표시 또는 응답 DTO에서 사용할 상태 설명입니다.
     */
    private final String description;

    /**
     * 관리자 상태 Enum 생성자입니다.
     *
     * @param description 상태 설명
     */
    AdminStatus(String description) {
        this.description = description;
    }

    /**
     * 문자열 값을 {@link AdminStatus}로 변환합니다.
     *
     * @param value 변환할 관리자 상태 문자열
     * @return 변환된 AdminStatus
     * @throws ServiceException 유효하지 않은 관리자 상태 값인 경우
     */
    public static AdminStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_ADMIN_STATUS);
        }

        try {
            return AdminStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ErrorCode.INVALID_ADMIN_STATUS);
        }
    }

    /**
     * 로그인 가능한 관리자 상태인지 검증합니다.
     *
     * <p>
     * Spring Security 적용 시 로그인 성공 전 단계에서 호출하여,
     * 인증 객체가 생성되기 전에 계정 상태를 검증하는 용도로 사용할 수 있습니다.
     * </p>
     *
     * @throws ServiceException 로그인할 수 없는 관리자 상태인 경우
     */
    public void validateLoginable() {
        switch (this) {
            case PENDING_APPROVAL -> throw new ServiceException(ErrorCode.ADMIN_PENDING_APPROVAL);
            case REJECTED -> throw new ServiceException(ErrorCode.ADMIN_REJECTED);
            case SUSPENDED -> throw new ServiceException(ErrorCode.ADMIN_SUSPENDED);
            case INACTIVE -> throw new ServiceException(ErrorCode.ADMIN_INACTIVE);
            case ACTIVE -> {
                // 로그인 가능한 정상 상태입니다.
            }
        }
    }

    /**
     * 로그인 가능한 상태인지 반환합니다.
     *
     * @return ACTIVE 상태이면 true
     */
    public boolean canLogin() {
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