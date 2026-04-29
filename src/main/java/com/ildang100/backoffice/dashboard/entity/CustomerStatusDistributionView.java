package com.ildang100.backoffice.dashboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/**
 * 고객 상태 분포 View Entity입니다.
 *
 * <p>
 * 고객 상태별 고객 수를 조회하기 위한 DB View와 매핑됩니다.
 * 차트 데이터 표시를 위한 조회 전용 엔티티입니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
@Entity
@Immutable
@Table(name = "customer_status_distribution_view")
public class CustomerStatusDistributionView {

    /**
     * 고객 상태입니다.
     *
     * <p>
     * ACTIVE, INACTIVE, SUSPENDED 등의 상태값이 사용됩니다.
     * View 조회 결과에서 상태값이 식별자 역할을 합니다.
     * </p>
     */
    @Id
    private String status;

    /**
     * 해당 상태의 고객 수입니다.
     */
    private Long customerCount;
}
