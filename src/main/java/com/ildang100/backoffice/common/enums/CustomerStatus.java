package com.ildang100.backoffice.common.enums;

/**
 * 고객 상태를 정의하는 Enum입니다.
 *
 * <p>고객 계정의 활성 여부를 나타냅니다.</p>
 *
 * <ul>
 *     <li>ACTIVE - 정상적으로 서비스 이용 가능</li>
 *     <li>INACTIVE - 비활성 상태</li>
 *     <li>SUSPENDED - 이용 제한 상태</li>
 * </ul>
 */
public enum CustomerStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}
