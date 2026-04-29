package com.ildang100.backoffice.dashboard.dto;

import com.ildang100.backoffice.dashboard.entity.ProductCategoryDistributionView;
import lombok.Getter;

/**
 * 상품 카테고리 분포 응답 DTO입니다.
 *
 * <p>
 * 상품 카테고리별 상품 수를 차트로 표시하기 위해 사용됩니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
public class ProductCategoryDistributionResponse {

    /**
     * 상품 카테고리입니다.
     */
    private final String category;

    /**
     * 해당 카테고리의 상품 수입니다.
     */
    private final Long productCount;

    private ProductCategoryDistributionResponse(String category, Long productCount) {
        this.category = category;
        this.productCount = productCount;
    }

    /**
     * View Entity를 응답 DTO로 변환합니다.
     *
     * @param view 상품 카테고리 분포 View Entity
     * @return 상품 카테고리 분포 응답 DTO
     */
    public static ProductCategoryDistributionResponse from(ProductCategoryDistributionView view) {
        return new ProductCategoryDistributionResponse(
                view.getCategory(),
                view.getProductCount()
        );
    }
}
