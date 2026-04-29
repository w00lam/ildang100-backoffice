package com.ildang100.backoffice.dashboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/**
 * 상품 카테고리 분포 View Entity입니다.
 *
 * <p>
 * 상품 카테고리별 상품 수를 조회하기 위한 DB View와 매핑됩니다.
 * 차트 데이터 표시를 위한 조회 전용 엔티티입니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
@Entity
@Immutable
@Table(name = "product_category_distribution_view")
public class ProductCategoryDistributionView {

    /**
     * 상품 카테고리입니다.
     *
     * <p>
     * View 조회 결과에서 카테고리 값이 식별자 역할을 합니다.
     * </p>
     */
    @Id
    private String category;

    /**
     * 해당 카테고리의 상품 수입니다.
     */
    private Long count;
}
