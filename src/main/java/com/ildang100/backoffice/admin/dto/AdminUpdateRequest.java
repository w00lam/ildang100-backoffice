package com.ildang100.backoffice.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 정보 수정 요청 DTO
 *
 * <p>
 * 관리자 기본 정보(이름, 이메일, 전화번호) 수정을 위한 요청 데이터를 담습니다.
 * </p>
 *
 * <p><b>제약 조건</b></p>
 * <ul>
 * <li>name: 필수 입력, 최대 30자</li>
 * <li>email: 필수 입력, 이메일 형식 준수, 최대 50자</li>
 * <li>tele: 필수 입력, 최대 20자</li>
 * </ul>
 *
 * @author 박채빈
 * @since 2026-04-27
 */
@Getter
@NoArgsConstructor
public class AdminUpdateRequest {
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
