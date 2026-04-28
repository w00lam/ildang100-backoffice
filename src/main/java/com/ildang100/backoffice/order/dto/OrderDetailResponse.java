package com.ildang100.backoffice.order.dto;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 상세 조회 응답 DTO입니다.
 *
 * <p>주문 기본 정보와 고객, 상품, 담당 관리자 정보를 함께 제공합니다.</p>
 */
@Getter
public class OrderDetailResponse {

    private final Long id;
    private final Long orderNumber;
    private final String customerName;
    private final String customerEmail;
    private final String productName;
    private final int quantity;
    private final int totalPrice;
    private final LocalDateTime createdAt;
    private final OrderStatus status;
    private final String adminName;
    private final String adminEmail;
    private final AdminRole adminRole;

    public OrderDetailResponse(
            Long id,
            Long orderNumber,
            String customerName,
            String customerEmail,
            String productName,
            int quantity,
            int totalPrice,
            LocalDateTime createdAt,
            OrderStatus status,
            String adminName,
            String adminEmail,
            AdminRole adminRole
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.status = status;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminRole = adminRole;
    }

    /**
     * 주문 엔티티를 주문 상세 응답 DTO로 변환합니다.
     *
     * @param order 주문 엔티티
     * @return 주문 상세 응답 DTO
     */
    public static OrderDetailResponse from(Order order) {
        Admin admin = order.getAdmin();

        return new OrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getStatus(),
                admin == null ? null : admin.getName(),
                admin == null ? null : admin.getEmail(),
                admin == null ? null : admin.getRole()
        );
    }
}
