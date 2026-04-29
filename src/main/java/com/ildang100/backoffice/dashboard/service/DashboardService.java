package com.ildang100.backoffice.dashboard.service;

import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.dashboard.dto.DashboardResponse;
import com.ildang100.backoffice.dashboard.dto.DashboardWidgetResponse;
import com.ildang100.backoffice.dashboard.entity.DashboardWidgetView;
import com.ildang100.backoffice.dashboard.repository.DashboardWidgetViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 대시보드 조회 비즈니스 로직을 처리하는 서비스입니다.
 *
 * @author 이우람
 * @since 2026-04-28
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardWidgetViewRepository dashboardWidgetViewRepository;

    /**
     * 대시보드 위젯 통계를 조회합니다.
     *
     * @return 대시보드 위젯 통계 응답
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDashBoard() {
        DashboardWidgetView widgetView = dashboardWidgetViewRepository.findById(1L).orElseThrow();

        DashboardWidgetResponse widgets = DashboardWidgetResponse.from(widgetView);

        // TODO: 이후 구현 예정
        // DashboardSummaryResponse summary;
        // DashboardChartResponse charts;
        // List<RecentOrderResponse> recentOrders;

        return DashboardResponse.of(widgets);
    }
}
