package com.ildang100.backoffice.order.dto;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 생성 응답 DTO입니다.
 */
@Getter
public class OrderCreateResponse {

    private final Long id;
    private final Long orderNumber;
    private final Long customerId;
    private final Long productId;
    private final int quantity;
    private final int unitPrice;
    private final int totalPrice;
    private final OrderStatus status;
    private final String adminName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private OrderCreateResponse(
            Long id,
            Long orderNumber,
            Long customerId,
            Long productId,
            int quantity,
            int unitPrice,
            int totalPrice,
            OrderStatus status,
            String adminName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.adminName = adminName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 주문 엔티티를 주문 생성 응답 DTO로 변환합니다.
     *
     * @param order 주문 엔티티
     * @return 주문 생성 응답 DTO
     */
    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getProduct().getId(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getAdmin() == null ? null : order.getAdmin().getName(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
