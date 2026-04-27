package com.ildang100.backoffice.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 고객 기본 정보 수정 요청 DTO입니다.
 */
@Getter
public class CustomerUpdateRequest {

    @Size(max = 30, message = "이름은 최대 30자까지 입력할 수 있습니다.")
    private String name;

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Size(max = 50, message = "이메일은 최대 50자까지 입력할 수 있습니다.")
    private String email;

    @Size(max = 30, message = "전화번호는 최대 30자까지 입력할 수 있습니다.")
    private String tele;
}
