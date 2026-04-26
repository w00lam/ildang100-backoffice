package com.ildang100.backoffice.customer.dto;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.customer.entity.Customer;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 고객 단건 정보를 표현하는 응답 DTO입니다.
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

    public CustomerResponse(
            Long id,
            String name,
            String email,
            String tele,
            CustomerStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tele = tele;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getTele(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
