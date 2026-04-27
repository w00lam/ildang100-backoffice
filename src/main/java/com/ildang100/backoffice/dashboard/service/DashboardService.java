package com.ildang100.backoffice.dashboard.service;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.customer.repository.CustomerRepository;
import com.ildang100.backoffice.dashboard.dto.DashboardResponse;
import com.ildang100.backoffice.dashboard.dto.SummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 대시보드 통계 조회 서비스입니다.
 *
 * @author 이우람
 * @since 2026-04-27
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerRepository customerRepository;

    /**
     * 대시보드 Summary 데이터를 조회합니다.
     *
     * <p>
     * 현재는 고객 통계만 제공하며,
     * 추후 관리자/상품/주문/리뷰 통계를 확장할 예정입니다.
     * </p>
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {

        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countByStatus(CustomerStatus.ACTIVE);

        SummaryResponse summary = SummaryResponse.of(totalCustomers, activeCustomers);

        return DashboardResponse.of(summary);
    }
}
