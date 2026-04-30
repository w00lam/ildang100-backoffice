package com.ildang100.backoffice.admin.dto;

import com.ildang100.backoffice.common.enums.AdminStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 상태 수정 요청 DTO
 *
 * <p>
 * 관리자의 활동 상태(Status)를 변경하기 위한 요청 데이터를 담습니다.
 * </p>
 *
 * <p><b>상태값 종류</b></p>
 * <ul>
 * <li>ACTIVE: 활동 중</li>
 * <li>INACTIVE: 비활동</li>
 * <li>SUSPENDED: 정지</li>
 * <li>PENDING_APPROVAL: 승인 대기</li>
 * <li>REJECTED: 거절</li>
 * </ul>
 *
 * @author 박채빈
 * @since 2026-04-28
 */
@Getter
@NoArgsConstructor
public class AdminStatusUpdateRequest {

    @NotNull(message = "변경할 상태값은 필수입니다.")
    private AdminStatus status;
}