package com.ildang100.backoffice.order.dto.response;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 목록에 표시할 주문 요약 응답 DTO입니다.
 */
@Getter
public class OrderInfoResponse {

    private final Long id;
    private final Long orderNumber;
    private final String customerName;
    private final String productName;
    private final int quantity;
    private final int totalPrice;
    private final LocalDateTime createdAt;
    private final OrderStatus status;
    private final String adminName;

    private OrderInfoResponse(
            Long id,
            Long orderNumber,
            String customerName,
            String productName,
            int quantity,
            int totalPrice,
            LocalDateTime createdAt,
            OrderStatus status,
            String adminName
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.status = status;
        this.adminName = adminName;
    }

    /**
     * 주문 엔티티를 주문 요약 응답 DTO로 변환합니다.
     *
     * @param order 주문 엔티티
     * @return 주문 요약 응답 DTO
     */
    public static OrderInfoResponse from(Order order) {
        return new OrderInfoResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getName(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getAdmin() == null ? null : order.getAdmin().getName()
        );
    }
}

