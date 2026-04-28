package com.ildang100.backoffice.dashboard.dto;

import lombok.Getter;

/**
 * 대시보드 통합 응답 DTO입니다.
 *
 * <p>
 * 관리자 백오피스 대시보드 화면에서 필요한 다양한 통계 데이터를
 * 하나의 API 응답으로 묶어 전달하기 위한 객체입니다.
 * </p>
 *
 * <p>
 * 현재는 위젯 통계({@link DashboardWidgetResponse})만 포함되어 있으며,
 * 이후 단계적으로 다음 데이터가 확장될 예정입니다:
 * <ul>
 *     <li>Summary: 관리자, 고객, 상품, 주문, 리뷰 요약 통계</li>
 *     <li>Charts: 분포 기반 차트 데이터 (평점, 상태, 카테고리 등)</li>
 *     <li>Recent Orders: 최근 주문 목록</li>
 * </ul>
 * </p>
 *
 * <p>
 * 해당 클래스는 대시보드 응답의 "컨테이너 역할"을 하며,
 * 기능 확장 시 필드를 추가하는 방식으로 점진적으로 확장됩니다.
 * </p>
 *
 * <p>
 * 불변(immutable) 객체로 설계되어 외부에서 상태 변경을 방지하며,
 * 생성은 정적 팩토리 메서드 {@link #of(DashboardWidgetResponse)}를 통해서만 가능합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-28
 */
@Getter
public class DashboardResponse {

    private final DashboardWidgetResponse widgets;

    // TODO: 이후 구현 예정
    // DashboardSummaryResponse summary;
    // DashboardChartResponse charts;
    // List<RecentOrderResponse> recentOrders;

    /**
     * TODO: 이후 구현 예정
     *
     * <p>
     * 향후 확장될 대시보드 구성 요소:
     * <ul>
     *     <li>DashboardSummaryResponse summary</li>
     *     <li>DashboardChartResponse charts</li>
     *     <li>List&lt;RecentOrderResponse&gt; recentOrders</li>
     * </ul>
     * </p>
     */

    private DashboardResponse(DashboardWidgetResponse widgets) {
        this.widgets = widgets;
    }

    /**
     * 대시보드 응답 객체를 생성하는 정적 팩토리 메서드입니다.
     *
     * <p>
     * 현재는 위젯 통계만 포함하여 응답을 생성합니다.
     * 향후 필드가 확장될 경우 해당 메서드도 함께 확장됩니다.
     * </p>
     *
     * @param widgets 대시보드 위젯 통계 데이터
     * @return DashboardResponse 생성된 응답 객체
     */
    public static DashboardResponse of(DashboardWidgetResponse widgets) {
        return new DashboardResponse(widgets);
    }
}
