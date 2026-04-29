package com.ildang100.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ildang100.backoffice.dashboard.entity.DashboardWidgetView;
import lombok.Getter;

/**
 * 대시보드 위젯 통계 응답 DTO입니다.
 *
 * <p>
 * 관리자 대시보드 화면에서 사용하는 주요 통계 데이터를 전달하기 위한 객체입니다.
 * DB View({@link DashboardWidgetView})를 기반으로 조회된 데이터를
 * 클라이언트에 전달하기 적합한 형태로 변환합니다.
 * </p>
 *
 * <p>
 * 해당 객체는 불변(immutable) 구조로 설계되어,
 * 생성 이후 데이터 변경을 방지하고 안정성을 보장합니다.
 * 생성은 정적 팩토리 메서드 {@link #from(DashboardWidgetView)}를 통해서만 가능합니다.
 * </p>
 *
 * <p>
 * 포함되는 데이터:
 * <ul>
 *     <li>총 매출 / 오늘 매출</li>
 *     <li>주문 상태별 개수 (준비중, 배송중, 배송완료)</li>
 *     <li>재고 상태별 상품 개수 (재고 부족, 품절)</li>
 * </ul>
 * </p>
 *
 * @author 이우람
 * @since 2026-04-28
 */
@Getter
@JsonPropertyOrder({
        "totalSales",
        "todaySales",
        "preparingOrders",
        "shippingOrders",
        "deliveredOrders",
        "lowStockProducts",
        "outOfStockProducts"
})
public class DashboardWidgetResponse {

    private final Long totalSales;
    private final Long todaySales;
    private final Long preparingOrders;
    private final Long shippingOrders;
    private final Long deliveredOrders;
    private final Long lowStockProducts;
    private final Long outOfStockProducts;

    private DashboardWidgetResponse(
            Long totalSales,
            Long todaySales,
            Long preparingOrders,
            Long shippingOrders,
            Long deliveredOrders,
            Long lowStockProducts,
            Long outOfStockProducts
    ) {
        this.totalSales = totalSales;
        this.todaySales = todaySales;
        this.preparingOrders = preparingOrders;
        this.shippingOrders = shippingOrders;
        this.deliveredOrders = deliveredOrders;
        this.lowStockProducts = lowStockProducts;
        this.outOfStockProducts = outOfStockProducts;
    }

    /**
     * {@link DashboardWidgetView} 엔티티를 {@link DashboardWidgetResponse}로 변환합니다.
     *
     * <p>
     * DB View에서 조회된 통계 데이터를 클라이언트 응답 DTO로 매핑합니다.
     * </p>
     *
     * @param view 대시보드 위젯 통계 View 엔티티
     * @return DashboardWidgetResponse 변환된 응답 객체
     */
    public static DashboardWidgetResponse from(DashboardWidgetView view) {
        return new DashboardWidgetResponse(
                view.getTotalSales(),
                view.getTodaySales(),
                view.getPreparingOrders(),
                view.getShippingOrders(),
                view.getDeliveredOrders(),
                view.getLowStockProducts(),
                view.getOutOfStockProducts()
        );
    }
}
