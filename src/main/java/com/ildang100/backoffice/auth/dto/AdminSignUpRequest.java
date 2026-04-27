package com.ildang100.backoffice.auth.dto;

import com.ildang100.backoffice.common.enums.AdminRole;
import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * 관리자 회원가입 요청 DTO입니다.
 *
 * <p>
 * 관리자 계정 생성 시 필요한 입력 값을 담습니다.
 * 모든 필드는 유효성 검증을 통해 검증되며,
 * 승인일(approvedAt)과 상태(status)는 서버에서 관리됩니다.
 * </p>
 *
 * <p><b>요청 필드</b></p>
 * <ul>
 *     <li>name: 관리자 이름</li>
 *     <li>email: 관리자 이메일 (로그인 ID)</li>
 *     <li>password: 관리자 비밀번호</li>
 *     <li>tele: 관리자 전화번호</li>
 *     <li>role: 관리자 권한</li>
 * </ul>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@Getter
public class AdminSignUpRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 최대 30자까지 입력할 수 있습니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 50, message = "이메일은 최대 50자까지 입력할 수 있습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하로 입력해야 합니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다."
    )
    @Size(max = 20, message = "전화번호는 최대 20자까지 입력할 수 있습니다.")
    private String tele;

    @NotNull(message = "관리자 권한은 필수입니다.")
    private AdminRole role;
}
