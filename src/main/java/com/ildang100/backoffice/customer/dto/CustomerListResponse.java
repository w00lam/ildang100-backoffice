package com.ildang100.backoffice.customer.dto;

import com.ildang100.backoffice.customer.entity.Customer;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 고객 목록 조회 응답 DTO입니다.
 *
 * <p>고객 목록과 페이지네이션 메타 정보를 함께 제공합니다.
 * {@code page}는 0부터 시작하는 페이지 번호입니다.</p>
 */
@Getter
public class CustomerListResponse {

    private final List<CustomerResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public CustomerListResponse(
            List<CustomerResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages)
    {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    /**
     * 고객 엔티티 페이지를 고객 목록 응답 DTO로 변환합니다.
     *
     * @param customers 고객 엔티티 페이지
     * @return 고객 목록 응답 DTO
     */
    public static CustomerListResponse from(Page<Customer> customers) {
        List<CustomerResponse> content = customers.getContent().stream()
                .map(CustomerResponse::from)
                .toList();

        return new CustomerListResponse(
                content,
                customers.getNumber(),
                customers.getSize(),
                customers.getTotalElements(),
                customers.getTotalPages()
        );
    }
}
