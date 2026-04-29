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
 * 대시보드 API 컨트롤러입니다.
 *
 * <p>
 * 관리자 백오피스에서 사용하는 대시보드 통계 데이터를 제공합니다.
 * 단일 API를 통해 위젯, 요약 통계, 차트 데이터, 최근 주문 목록을
 * 통합적으로 조회할 수 있도록 설계되었습니다.
 * </p>
 *
 * <p>
 * 현재는 위젯 통계 기능부터 우선 구현되었으며,
 * 이후 단계적으로 Summary, Charts, Recent Orders 기능이 확장될 예정입니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-28
 */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 통합 조회 API
     *
     * <p>
     * 로그인된 관리자를 기준으로 대시보드에 필요한 모든 통계 데이터를 조회합니다.
     * </p>
     *
     * <p>
     * 반환 데이터 구성:
     * <ul>
     *     <li>Summary: 관리자, 고객, 상품, 주문, 리뷰 요약 통계</li>
     *     <li>Widgets: 매출, 주문 상태, 재고 상태 통계</li>
     *     <li>Charts: 분포 기반 차트 데이터 (추후 추가)</li>
     *     <li>Recent Orders: 최근 주문 목록 (추후 추가)</li>
     * </ul>
     * </p>
     *
     * <p>
     * 현재는 Widgets 데이터만 포함되어 반환됩니다.
     * </p>
     *
     * @param session 현재 로그인된 관리자 세션
     * @return 대시보드 통합 통계 응답
     */
    @GetMapping("/admin/dashboard")
    public CommonApiResponse<DashboardResponse> getDashboard(HttpSession session) {

        SessionUtils.getLoginAdmin(session); // security 기반으로 전환 시 수정 예정

        DashboardResponse response = dashboardService.getDashBoard();

        return CommonApiResponse.success(HttpStatus.OK, "대시보드 조회가 완료되었습니다.", response);
    }
}
