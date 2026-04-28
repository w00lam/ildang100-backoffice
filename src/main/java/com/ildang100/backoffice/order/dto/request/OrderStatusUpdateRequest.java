package com.ildang100.backoffice.order.dto.request;

import com.ildang100.backoffice.common.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderStatusUpdateRequest {

    @NotNull(message = "주문 상태는 필수입니다.")
    private OrderStatus status;
}
