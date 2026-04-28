package com.ildang100.backoffice.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 가입 승인/거절 요청 DTO
 *
 * <p>
 * 슈퍼 관리자가 대기 중인 관리자의 가입 요청을 처리하기 위한 데이터를 담습니다.
 * </p>
 *
 * @author 박채빈
 * @since 2026-04-28
 */
@Getter
@NoArgsConstructor
public class AdminApprovalRequest {

    @NotNull(message = "승인 여부는 필수 선택 사항입니다.")
    private Boolean isApproved;

    @Size(max = 100, message = "거절 사유는 최대 100자까지 입력 가능합니다.")
    private String rejectReason;
}