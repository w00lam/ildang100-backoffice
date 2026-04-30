package com.ildang100.backoffice.customer.dto;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.customer.entity.Customer;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 고객 단건 정보를 표현하는 응답 DTO입니다.
 *
 * <p>고객 기본 정보와 취소 주문을 제외한 누적 주문 통계를 함께 제공합니다.</p>
 */
@Getter
public class CustomerResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String tele;
    private final CustomerStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long totalOrderCount;
    private final long totalOrderAmount;

    private CustomerResponse(
            Long id,
            String name,
            String email,
            String tele,
            CustomerStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long totalOrderCount,
            long totalOrderAmount
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tele = tele;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalOrderCount = totalOrderCount;
        this.totalOrderAmount = totalOrderAmount;
    }

    public static CustomerResponse from(Customer customer) {
        return from(customer, CustomerOrderStats.empty(customer.getId()));
    }

    /**
     * 고객 엔티티와 주문 통계를 고객 응답 DTO로 변환합니다.
     *
     * @param customer 고객 엔티티
     * @param orderStats 고객별 주문 통계
     * @return 고객 응답 DTO
     */
    public static CustomerResponse from(Customer customer, CustomerOrderStats orderStats) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getTele(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                orderStats.getTotalOrderCount(),
                orderStats.getTotalOrderAmount()
        );
    }
}
