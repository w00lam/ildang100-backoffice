package com.ildang100.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

/**
 * 대시보드 통합 응답 DTO입니다.
 *
 * <p>
 * 관리자 백오피스 대시보드 화면에서 필요한 다양한 통계 데이터를
 * 하나의 API 응답으로 묶어 전달하기 위한 객체입니다.
 * </p>
 *
 * <p>
 * 현재는 다음 데이터를 포함합니다:
 * <ul>
 *     <li>{@link DashboardSummaryResponse} : 요약 통계</li>
 *     <li>{@link DashboardWidgetResponse} : 위젯 통계</li>
 *     <li>{@link DashboardChartResponse} : 차트 통계</li>
 *     <li>{@link RecentOrderResponse} : 최근 주문 목록</li>
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
 * 생성은 정적 팩토리 메서드
 * {@link #of(DashboardSummaryResponse, DashboardWidgetResponse, DashboardChartResponse, List)}
 * 를 통해서만 가능합니다.
 * </p>
 *
 * <p>
 * 최근 주문 목록은 {@code List<RecentOrderResponse>} 형태로 전달됩니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-28
 */
@Getter
@JsonPropertyOrder({
        "summary",
        "widgets",
        "charts",
        "recentOrders"
})
public class DashboardResponse {

    private final DashboardSummaryResponse summary;
    private final DashboardWidgetResponse widgets;
    private final DashboardChartResponse charts;
    private final List<RecentOrderResponse> recentOrders;

    private DashboardResponse(
            DashboardSummaryResponse summary,
            DashboardWidgetResponse widgets,
            DashboardChartResponse charts,
            List<RecentOrderResponse> recentOrders
    ) {
        this.summary = summary;
        this.widgets = widgets;
        this.charts = charts;
        this.recentOrders = recentOrders;
    }

    /**
     * 대시보드 응답 객체를 생성하는 정적 팩토리 메서드입니다.
     *
     * <p>
     * 현재는 위젯 통계만 포함하여 응답을 생성합니다.
     * 향후 필드가 확장될 경우 해당 메서드도 함께 확장됩니다.
     * </p>
     *
     * @param summary      대시보드 요약 통계 데이터
     * @param widgets      대시보드 위젯 통계 데이터
     * @param charts       대시보드 차트 통계 데이터
     * @param recentOrders 대시보드 최근 주문 리스트 데이터
     * @return DashboardResponse 생성된 응답 객체
     */
    public static DashboardResponse of(
            DashboardSummaryResponse summary,
            DashboardWidgetResponse widgets,
            DashboardChartResponse charts,
            List<RecentOrderResponse> recentOrders
    ) {
        return new DashboardResponse(summary, widgets, charts, recentOrders);
    }
}
