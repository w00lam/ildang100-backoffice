package com.ildang100.backoffice.customer.dto;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 고객 상태 수정 요청 DTO입니다.
 */
@Getter
public class CustomerStatusUpdateRequest {

    @NotNull(message = "고객 상태는 필수입니다.")
    private CustomerStatus status;
}
