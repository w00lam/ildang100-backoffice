package com.ildang100.backoffice.dashboard.dto;

import lombok.Getter;

/**
 * 대시보드 응답 DTO입니다.
 *
 * <p>
 * 현재는 Summary 통계만 포함합니다.
 * 추후 Widgets, Charts, Recent Orders 영역을 확장할 수 있습니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@Getter
public class DashboardResponse {

    private final SummaryResponse summary;

    private DashboardResponse(SummaryResponse summary) {
        this.summary = summary;
    }

    public static DashboardResponse of(SummaryResponse summary) {
        return new DashboardResponse(summary);
    }
}
