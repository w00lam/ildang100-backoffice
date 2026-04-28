package com.ildang100.backoffice.order.dto.request;

import com.ildang100.backoffice.common.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 주문 상태 수정 요청 DTO입니다.
 */
@Getter
public class OrderStatusUpdateRequest {

    @NotNull(message = "주문 상태는 필수입니다.")
    private OrderStatus status;
}
