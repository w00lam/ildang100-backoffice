package com.ildang100.backoffice.dashboard.controller;

import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.dashboard.dto.DashboardResponse;
import com.ildang100.backoffice.dashboard.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 대시보드 API를 담당하는 컨트롤러입니다.
 *
 * <p>
 * 전체 시스템의 주요 통계 데이터를 조회하는 API를 제공합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 통계 조회 API
     *
     * <p>
     * 전체 관리자, 고객, 상품, 주문, 리뷰 등의 통계 데이터를 조회합니다.
     * 현재는 구현 가능한 고객 통계부터 제공하며,
     * 나머지 통계는 관련 도메인 구현 후 확장 예정입니다.
     * </p>
     *
     * @return 대시보드 통계 응답
     */
    @GetMapping("/admin/dashboard")
    public CommonApiResponse<DashboardResponse> getDashboard(HttpSession session) {
        SessionUtils.getLoginAdmin(session);

        DashboardResponse response = dashboardService.getDashboard();

        return CommonApiResponse.success(HttpStatus.OK, "대시보드 조회가 완료되었습니다.", response);
    }
}
