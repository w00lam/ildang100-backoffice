package com.ildang100.backoffice.common.enums;

/**
 * 관리자 상태를 정의하는 Enum입니다.
 *
 * <p>관리자 계정의 활성 여부 및 승인 상태를 나타냅니다.</p>
 *
 * <ul>
 *     <li>ACTIVE - 정상적으로 사용 가능한 상태</li>
 *     <li>INACTIVE - 비활성화된 상태</li>
 *     <li>SUSPENDED - 이용이 제한된 상태</li>
 *     <li>PENDING_APPROVAL - 승인 대기 상태</li>
 *     <li>REJECTED - 가입 요청이 거부된 상태</li>
 * </ul>
 */
public enum AdminStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_APPROVAL,
    REJECTED
}
