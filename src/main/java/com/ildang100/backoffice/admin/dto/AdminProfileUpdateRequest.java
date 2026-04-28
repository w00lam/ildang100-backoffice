package com.ildang100.backoffice.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 프로필 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class AdminProfileUpdateRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 최대 30자까지 입력할 수 있습니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 50, message = "이메일은 최대 50자까지 입력할 수 있습니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다."
    )
    @Size(max = 20, message = "전화번호는 최대 20자까지 입력할 수 있습니다.")
    private String tele;
}