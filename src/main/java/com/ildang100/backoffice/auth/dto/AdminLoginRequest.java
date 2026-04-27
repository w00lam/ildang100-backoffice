package com.ildang100.backoffice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 관리자 로그인 요청 DTO입니다.
 *
 * <p>
 * 로그인 시 필요한 이메일과 비밀번호를 전달받습니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@Getter
public class AdminLoginRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
