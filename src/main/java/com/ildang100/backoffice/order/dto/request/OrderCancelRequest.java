package com.ildang100.backoffice.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 주문 취소 요청 DTO입니다.
 */
@Getter
public class OrderCancelRequest {

    @NotBlank(message = "주문 취소 사유는 필수입니다.")
    @Size(max = 255, message = "주문 취소 사유는 최대 255자까지 입력할 수 있습니다.")
    private String cancelReason;
}
