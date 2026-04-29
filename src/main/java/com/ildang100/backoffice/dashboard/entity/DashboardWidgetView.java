package com.ildang100.backoffice.dashboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/**
 * 대시보드 위젯 통계 View Entity입니다.
 *
 * <p>
 * dashboard_widget_view를 기반으로 조회 전용으로 사용됩니다.
 * DB View이므로 insert/update는 수행하지 않습니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-28
 */
@Getter
@Entity
@Immutable
@Table(name = "dashboard_widget_view")
public class DashboardWidgetView {

    /**
     * JPA 매핑을 위한 PK (View 특성상 고정값)
     */
    @Id
    private Long id;

    /**
     * 총 매출
     */
    private Long totalSales;

    /**
     * 오늘 매출
     */
    private Long todaySales;

    /**
     * 준비중 주문 수
     */
    private Long preparingOrders;

    /**
     * 배송중 주문 수
     */
    private Long shippingOrders;

    /**
     * 배송완료 주문 수
     */
    private Long deliveredOrders;

    /**
     * 재고 부족 상품 수 (1~5개)
     */
    private Long lowStockProducts;

    /**
     * 품절 상품 수
     */
    private Long outOfStockProducts;
}
