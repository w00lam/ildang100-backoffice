package com.ildang100.backoffice.dashboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/**
 * 대시보드 Summary 통계 View를 매핑하는 엔티티입니다.
 *
 * <p>
 * dashboard_summary_view를 기반으로 조회 전용으로 사용되며,
 * 관리자/고객/상품/주문/리뷰에 대한 전체 통계를 제공합니다.
 * </p>
 *
 * <p>
 * 해당 엔티티는 DB View와 매핑되므로 insert/update가 불가능하며,
 * Hibernate의 변경 감지 대상에서 제외하기 위해 {@code @Immutable}을 사용합니다.
 * </p>
 *
 * <p>
 * View는 항상 단일 row를 반환하도록 설계되어 있으며,
 * JPA 매핑을 위해 고정 PK(id=1)를 사용합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
@Entity
@Immutable
@Table(name = "dashboard_summary_view")
public class DashboardSummaryView {

    /**
     * JPA 매핑을 위한 PK (View 특성상 고정값)
     */
    @Id
    private Long id;

    /** 전체 관리자 수 */
    private Long totalAdminCount;

    /** 활성 관리자 수 */
    private Long activeAdminCount;

    /** 전체 고객 수 */
    private Long totalCustomerCount;

    /** 활성 고객 수 */
    private Long activeCustomerCount;

    /** 전체 상품 수 (삭제 제외) */
    private Long totalProductCount;

    /** 재고 부족 상품 수 (재고 1~5개) */
    private Long lowStockProductCount;

    /** 전체 주문 수 (취소 및 삭제 제외) */
    private Long totalOrderCount;

    /** 오늘 주문 수 */
    private Long todayOrderCount;

    /** 전체 리뷰 수 */
    private Long totalReviewCount;

    /** 평균 평점 */
    private Double averageRating;
}
