package com.ildang100.backoffice.order.dto;

import com.ildang100.backoffice.order.entity.Order;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

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