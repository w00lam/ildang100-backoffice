package com.ildang100.backoffice.common.enums;

/**
 * 관리자 역할을 정의하는 Enum입니다.
 *
 * <p>관리자의 권한 범위를 구분하기 위해 사용됩니다.</p>
 *
 * <ul>
 *     <li>SUPER_ADMIN - 전체 시스템 관리 권한을 가진 최고 관리자</li>
 *     <li>OPERATIONS_ADMIN - 운영 관련 기능을 담당하는 관리자</li>
 *     <li>CS_ADMIN - 고객 응대 및 문의 처리를 담당하는 관리자</li>
 * </ul>
 */
public enum AdminRole {
    SUPER_ADMIN,
    OPERATIONS_ADMIN,
    CS_ADMIN
}
