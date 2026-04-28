package com.ildang100.backoffice.order.dto.response;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 취소 응답 DTO입니다.
 */
@Getter
public class OrderCancelResponse {

    private final Long id;
    private final OrderStatus status;
    private final String cancelReason;
    private final LocalDateTime updatedAt;

    private OrderCancelResponse(
            Long id,
            OrderStatus status,
            String cancelReason,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.status = status;
        this.cancelReason = cancelReason;
        this.updatedAt = updatedAt;
    }

    /**
     * 주문 엔티티를 주문 취소 응답 DTO로 변환합니다.
     *
     * @param order 주문 엔티티
     * @return 주문 취소 응답 DTO
     */
    public static OrderCancelResponse from(Order order) {
        return new OrderCancelResponse(
                order.getId(),
                order.getStatus(),
                order.getCancelReason(),
                order.getUpdatedAt()
        );
    }
}
