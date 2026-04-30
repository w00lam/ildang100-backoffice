package com.ildang100.backoffice.admin.dto;

import com.ildang100.backoffice.common.enums.AdminRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 역할(Role) 수정 요청 DTO
 *
 * <p>
 * 관리자의 권한 등급을 변경하기 위한 요청 데이터를 담습니다.
 * </p>
 *
 * @author Park Chae-bin
 * @since 2026-04-28
 */
@Getter
@NoArgsConstructor
public class AdminRoleUpdateRequest {

    @NotNull(message = "변경할 관리자 권한은 필수입니다.")
    private AdminRole role;
}