package com.ildang100.backoffice.dashboard.dto;

import com.ildang100.backoffice.dashboard.entity.CustomerStatusDistributionView;
import lombok.Getter;

/**
 * 고객 상태 분포 응답 DTO입니다.
 *
 * <p>
 * 고객 상태별 고객 수를 차트로 표시하기 위해 사용됩니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
public class CustomerStatusDistributionResponse {

    /**
     * 고객 상태입니다.
     */
    private final String status;

    /**
     * 해당 상태의 고객 수입니다.
     */
    private final Long customerCount;

    private CustomerStatusDistributionResponse(String status, Long customerCount) {
        this.status = status;
        this.customerCount = customerCount;
    }

    /**
     * View Entity를 응답 DTO로 변환합니다.
     *
     * @param view 고객 상태 분포 View Entity
     * @return 고객 상태 분포 응답 DTO
     */
    public static CustomerStatusDistributionResponse from(CustomerStatusDistributionView view) {
        return new CustomerStatusDistributionResponse(
                view.getStatus(),
                view.getCustomerCount()
        );
    }
}
