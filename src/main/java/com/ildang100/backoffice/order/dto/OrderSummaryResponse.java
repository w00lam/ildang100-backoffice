package com.ildang100.backoffice.order.dto;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderSummaryResponse {

    private final Long id;
    private final Long orderNumber;
    private final String customerName;
    private final String productName;
    private final int quantity;
    private final int totalPrice;
    private final LocalDateTime createdAt;
    private final OrderStatus status;
    private final String adminName;

    private OrderSummaryResponse(
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

    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
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

