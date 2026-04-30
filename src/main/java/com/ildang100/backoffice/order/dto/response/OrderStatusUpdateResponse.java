package com.ildang100.backoffice.order.dto.response;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 상태 수정 응답 DTO입니다.
 */
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

    /**
     * 주문 엔티티를 주문 상태 수정 응답 DTO로 변환합니다.
     *
     * @param order 주문 엔티티
     * @return 주문 상태 수정 응답 DTO
     */
    public static OrderStatusUpdateResponse from(Order order) {
        return new OrderStatusUpdateResponse(
                order.getId(),
                order.getStatus(),
                order.getUpdatedAt()
        );
    }
}
