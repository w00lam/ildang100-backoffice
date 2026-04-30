package com.ildang100.backoffice.dashboard.service;

import com.ildang100.backoffice.dashboard.dto.*;
import com.ildang100.backoffice.dashboard.entity.DashboardSummaryView;
import com.ildang100.backoffice.dashboard.entity.DashboardWidgetView;
import com.ildang100.backoffice.dashboard.repository.*;
import com.ildang100.backoffice.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 대시보드 조회 비즈니스 로직을 처리하는 서비스입니다.
 *
 * @author 이우람
 * @since 2026-04-28
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardSummaryViewRepository dashBoardSummaryViewRepository;
    private final DashboardWidgetViewRepository dashboardWidgetViewRepository;
    private final ReviewRatingDistributionViewRepository reviewRatingDistributionViewRepository;
    private final CustomerStatusDistributionViewRepository customerStatusDistributionViewRepository;
    private final ProductCategoryDistributionViewRepository productCategoryDistributionViewRepository;
    private final OrderService orderService;

    /**
     * 대시보드 위젯 통계를 조회합니다.
     *
     * @return 대시보드 위젯 통계 응답
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDashBoard() {

        DashboardSummaryResponse summary = getSummary();
        DashboardWidgetResponse widgets = getWidgets();
        DashboardChartResponse charts = getCharts();
        List<RecentOrderResponse> recentOrders = orderService.getRecentOrdersForDashboard();

        return DashboardResponse.of(summary, widgets, charts, recentOrders);
    }

    /**
     * Summary 통계를 조회합니다.
     */
    private DashboardSummaryResponse getSummary() {
        DashboardSummaryView summaryView = dashBoardSummaryViewRepository.findById(1L).orElseThrow();

        return DashboardSummaryResponse.from(summaryView);
    }

    /**
     * Widget 통계를 조회합니다.
     */
    private DashboardWidgetResponse getWidgets() {
        DashboardWidgetView widgetView = dashboardWidgetViewRepository.findById(1L).orElseThrow();

        return DashboardWidgetResponse.from(widgetView);
    }

    /**
     * Chart 통계를 조회합니다.
     */
    private DashboardChartResponse getCharts() {
        return DashboardChartResponse.of(
                reviewRatingDistributionViewRepository.findAll(Sort.by(Sort.Direction.ASC, "rating"))
                        .stream()
                        .map(ReviewRatingDistributionResponse::from)
                        .toList(),

                customerStatusDistributionViewRepository.findAll()
                        .stream()
                        .map(CustomerStatusDistributionResponse::from)
                        .toList(),

                productCategoryDistributionViewRepository.findAll()
                        .stream()
                        .map(ProductCategoryDistributionResponse::from)
                        .toList()
        );
    }
}
