package com.ildang100.backoffice.dashboard.controller;

import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.dashboard.dto.DashboardResponse;
import com.ildang100.backoffice.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 대시보드 API 컨트롤러입니다.
 *
 * <p>대시보드 조회 API는 JWT 인증을 통과한 관리자만 접근할 수 있습니다.</p>
 */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드에 필요한 통합 통계 데이터를 조회합니다.
     */
    @GetMapping("/admin/dashboard")
    public CommonApiResponse<DashboardResponse> getDashboard() {
        DashboardResponse response = dashboardService.getDashBoard();

        return CommonApiResponse.success(HttpStatus.OK, "대시보드 조회가 완료되었습니다.", response);
    }
}
