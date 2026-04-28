package com.ildang100.backoffice.order.dto;

import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 주문 목록 조회 응답 DTO입니다.
 *
 * <p>주문 요약 목록과 페이지네이션 정보를 함께 제공합니다.
 * {@code page}는 1부터 시작하는 페이지 번호입니다.</p>
 */
@Getter
public class OrderListResponse {

    private final List<OrderSummaryResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    private OrderListResponse(
            List<OrderSummaryResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    /**
     * 주문 엔티티 페이지를 주문 목록 응답 DTO로 변환합니다.
     *
     * @param orders 주문 엔티티 페이지
     * @return 주문 목록 응답 DTO
     */
    public static OrderListResponse from(Page<Order> orders) {
        return new OrderListResponse(
                orders.getContent().stream()
                        .map(OrderSummaryResponse::from)
                        .toList(),
                orders.getNumber() + 1,
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages()
        );
    }
}
