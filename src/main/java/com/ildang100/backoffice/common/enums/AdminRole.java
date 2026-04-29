package com.ildang100.backoffice.common.enums;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;

/**
 * 관리자 역할을 정의하는 Enum입니다.
 *
 * <p>
 * 관리자 권한을 구분하기 위해 사용됩니다.
 * 이후 Spring Security 기반 인증/인가를 적용할 예정이며,
 * 해당 Enum은 Security 권한 문자열(GrantedAuthority)로 변환될 수 있습니다.
 * </p>
 *
 * <p>
 * 실제 API 접근 권한 검사는 Enum 내부에서 직접 처리하지 않고,
 * SecurityConfig 또는 {@code @PreAuthorize}를 통해 처리하는 것을 기준으로 합니다.
 * </p>
 *
 * <ul>
 *     <li>SUPER_ADMIN - 전체 시스템 관리 권한을 가진 최고 관리자</li>
 *     <li>OPERATIONS_ADMIN - 운영 관련 기능을 담당하는 관리자</li>
 *     <li>CS_ADMIN - 고객 응대 및 문의 처리를 담당하는 관리자</li>
 * </ul>
 *
 * @author 이우람
 * @since 2026-04-25
 */
public enum AdminRole {

    /**
     * 전체 시스템 관리 권한을 가진 최고 관리자입니다.
     */
    SUPER_ADMIN("최고 관리자"),

    /**
     * 상품, 주문 등 운영 관련 기능을 담당하는 관리자입니다.
     */
    OPERATIONS_ADMIN("운영 관리자"),

    /**
     * 고객 문의 및 응대 관련 기능을 담당하는 관리자입니다.
     */
    CS_ADMIN("고객 응대 관리자");

    /**
     * 화면 표시 또는 응답 DTO에서 사용할 역할 설명입니다.
     */
    private final String description;

    /**
     * 관리자 역할 Enum 생성자입니다.
     *
     * @param description 역할 설명
     */
    AdminRole(String description) {
        this.description = description;
    }

    /**
     * 문자열 값을 {@link AdminRole}로 변환합니다.
     *
     * <p>
     * 요청 파라미터, DTO, JWT Payload 등에서 전달받은 문자열 값을
     * 안전하게 Enum으로 변환하기 위해 사용합니다.
     * </p>
     *
     * @param value 변환할 관리자 역할 문자열
     * @return 변환된 AdminRole
     * @throws ServiceException 유효하지 않은 관리자 역할 값인 경우
     */
    public static AdminRole from(String value) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_ROLE);
        }

        try {
            return AdminRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ErrorCode.INVALID_ROLE);
        }
    }

    /**
     * Spring Security에서 사용하는 권한 문자열로 변환합니다.
     *
     * <p>
     * Spring Security의 {@code hasRole("SUPER_ADMIN")}는 내부적으로
     * {@code ROLE_SUPER_ADMIN} 권한을 확인합니다.
     * 따라서 JWT 인증 필터에서 {@code SimpleGrantedAuthority}를 만들 때 사용합니다.
     * </p>
     *
     * <pre>
     * new SimpleGrantedAuthority(role.toAuthority())
     * </pre>
     *
     * @return ROLE_ prefix가 포함된 권한 문자열
     */
    public String toAuthority() {
        return "ROLE_" + this.name();
    }

    /**
     * 역할 설명을 반환합니다.
     *
     * @return 역할 설명
     */
    public String getDescription() {
        return description;
    }
}