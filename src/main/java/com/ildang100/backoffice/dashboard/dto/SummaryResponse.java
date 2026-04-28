package com.ildang100.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

/**
 * 대시보드 Summary 통계 응답 DTO입니다.
 *
 * <p>
 * 대시보드 상단에 표시할 주요 요약 통계를 제공합니다.
 * 현재 단계에서는 관리자, 고객, 상품 통계만 우선 제공합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@Getter
@JsonPropertyOrder({
        "totalAdmins",
        "activeAdmins",
        "totalCustomers",
        "activeCustomers",
        "totalProducts",
        "lowStockProducts",

})
public class SummaryResponse {

    private final Long totalAdmins;
    private final Long activeAdmins;
    private final Long totalCustomers;
    private final Long activeCustomers;
    private final Long totalProducts;
    private final Long lowStockProducts;
    private final Long totalOrders;
    private final Long todayOrders;

    private SummaryResponse(
            Long totalAdmins,
            Long activeAdmins,
            Long totalCustomers,
            Long activeCustomers,
            Long totalProducts,
            Long lowStockProducts,
            Long totalOrders,
            Long todayOrders
    ) {
        this.totalAdmins = totalAdmins;
        this.activeAdmins = activeAdmins;
        this.totalCustomers = totalCustomers;
        this.activeCustomers = activeCustomers;
        this.totalProducts = totalProducts;
        this.lowStockProducts = lowStockProducts;
        this.totalOrders = totalOrders;
        this.todayOrders = todayOrders;
    }

    /**
     * 현재 단계에서 관리자, 고객, 상품, 주문 통계만 반환하는 팩토리 메서드입니다.
     */
    public static SummaryResponse of(
            Long totalAdmins,
            Long activeAdmins,
            Long totalCustomers,
            Long activeCustomers,
            Long totalProducts,
            Long lowStockProducts,
            Long totalOrders,
            Long todayOrders
    ) {
        return new SummaryResponse(
                totalAdmins,
                activeAdmins,
                totalCustomers,
                activeCustomers,
                totalProducts,
                lowStockProducts,
                totalOrders,
                todayOrders
        );
    }
}
