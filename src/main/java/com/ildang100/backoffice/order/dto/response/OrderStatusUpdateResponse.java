package com.ildang100.backoffice.order.dto.response;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderStatusUpdateResponse {
    private final Long id;
    private final OrderStatus status;
    private final LocalDateTime updatedAt;

    private OrderStatusUpdateResponse(
            Long id,
            OrderStatus status,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public static OrderStatusUpdateResponse from(Order order) {
        return new OrderStatusUpdateResponse(
                order.getId(),
                order.getStatus(),
                order.getUpdatedAt()
        );
    }
}
