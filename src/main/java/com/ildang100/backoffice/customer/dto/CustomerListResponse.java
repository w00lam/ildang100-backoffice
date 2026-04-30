package com.ildang100.backoffice.customer.dto;

import com.ildang100.backoffice.customer.entity.Customer;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 고객 목록 조회 응답 DTO입니다.
 *
 * <p>고객 목록과 페이지네이션 메타 정보를 함께 제공합니다.
 * {@code page}는 1부터 시작하는 페이지 번호입니다.</p>
 */
@Getter
public class CustomerListResponse {

    private final List<CustomerResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    private CustomerListResponse(
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
        return from(customers, Map.of());
    }

    /**
     * 고객 엔티티 페이지와 고객별 주문 통계를 고객 목록 응답 DTO로 변환합니다.
     *
     * <p>주문 통계가 없는 고객은 주문 건수와 주문 금액을 0으로 응답합니다.</p>
     *
     * @param customers 고객 엔티티 페이지
     * @param orderStatsMap 고객 ID를 key로 하는 주문 통계 Map
     * @return 고객 목록 응답 DTO
     */
    public static CustomerListResponse from(
            Page<Customer> customers,
            Map<Long, CustomerOrderStats> orderStatsMap
    ) {
        List<CustomerResponse> content = new ArrayList<>();

        for (Customer customer : customers.getContent()) {
            CustomerOrderStats orderStats = orderStatsMap.getOrDefault(
                    customer.getId(),
                    CustomerOrderStats.empty(customer.getId())
            );

            content.add(CustomerResponse.from(customer, orderStats));
        }

        return new CustomerListResponse(
                content,
                customers.getNumber() + 1,
                customers.getSize(),
                customers.getTotalElements(),
                customers.getTotalPages()
        );
    }
}
