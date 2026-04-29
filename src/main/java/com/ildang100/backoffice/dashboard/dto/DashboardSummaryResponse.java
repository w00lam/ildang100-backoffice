package com.ildang100.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ildang100.backoffice.dashboard.entity.DashboardSummaryView;
import lombok.Getter;

/**
 * 대시보드 Summary 통계 응답 DTO입니다.
 *
 * <p>
 * dashboard_summary_view 엔티티를 기반으로 생성되며,
 * 외부 API 응답 전용 데이터 구조로 사용됩니다.
 * </p>
 *
 * <p>
 * 엔티티를 직접 반환하지 않고 DTO로 변환하는 이유:
 * <ul>
 *     <li>응답 구조를 명확하게 통제하기 위함</li>
 *     <li>도메인 모델과 API 스펙을 분리하기 위함</li>
 *     <li>추후 필드 추가/변경 시 유연하게 대응하기 위함</li>
 * </ul>
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
@JsonPropertyOrder({
        "totalAdmins",
        "activeAdmins",
        "totalCustomers",
        "activeCustomers",
        "totalProducts",
        "lowStockProducts",
        "totalOrders",
        "todayOrders",
        "totalReviews",
        "averageRating"
})
public class DashboardSummaryResponse {

    private final Long totalAdmins;
    private final Long activeAdmins;
    private final Long totalCustomers;
    private final Long activeCustomers;
    private final Long totalProducts;
    private final Long lowStockProducts;
    private final Long totalOrders;
    private final Long todayOrders;
    private final Long totalReviews;
    private final Double averageRating;

    private DashboardSummaryResponse(
            Long totalAdmins,
            Long activeAdmins,
            Long totalCustomers,
            Long activeCustomers,
            Long totalProducts,
            Long lowStockProducts,
            Long totalOrders,
            Long todayOrders,
            Long totalReviews,
            Double averageRating
    ) {
        this.totalAdmins = totalAdmins;
        this.activeAdmins = activeAdmins;
        this.totalCustomers = totalCustomers;
        this.activeCustomers = activeCustomers;
        this.totalProducts = totalProducts;
        this.lowStockProducts = lowStockProducts;
        this.totalOrders = totalOrders;
        this.todayOrders = todayOrders;
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
    }

    /**
     * {@link DashboardSummaryView} 엔티티를 {@link DashboardSummaryResponse}로 변환합니다.
     *
     * <p>
     * DB View에서 조회된 통계 데이터를 클라이언트 응답 DTO로 매핑합니다.
     * </p>
     *
     * @param view 대시보드 요약 통계 View 엔티티
     * @return DashboardSummaryResponse 변환된 응답 객체
     */
    public static DashboardSummaryResponse from(DashboardSummaryView view) {
        return new DashboardSummaryResponse(
                view.getTotalAdminCount(),
                view.getActiveAdminCount(),
                view.getTotalCustomerCount(),
                view.getActiveCustomerCount(),
                view.getTotalProductCount(),
                view.getLowStockProductCount(),
                view.getTotalOrderCount(),
                view.getTodayOrderCount(),
                view.getTotalReviewCount(),
                view.getAverageRating()
        );
    }
}
