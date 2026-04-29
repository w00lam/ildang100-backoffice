package com.ildang100.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

/**
 * 대시보드 차트 통계 응답 DTO입니다.
 *
 * <p>
 * 대시보드에서 사용하는 차트 데이터를 하나로 묶어 전달합니다.
 * </p>
 *
 * <p>
 * 포함 데이터:
 * <ul>
 *     <li>리뷰 평점 분포</li>
 *     <li>고객 상태 분포</li>
 *     <li>상품 카테고리 분포</li>
 * </ul>
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
@JsonPropertyOrder({
        "reviewRatings",
        "customerStatuses",
        "productCategories"
})
public class DashboardChartResponse {

    private final List<ReviewRatingDistributionResponse> reviewRatings;
    private final List<CustomerStatusDistributionResponse> customerStatuses;
    private final List<ProductCategoryDistributionResponse> productCategories;

    private DashboardChartResponse(
            List<ReviewRatingDistributionResponse> reviewRatings,
            List<CustomerStatusDistributionResponse> customerStatuses,
            List<ProductCategoryDistributionResponse> productCategories
    ) {
        this.reviewRatings = reviewRatings;
        this.customerStatuses = customerStatuses;
        this.productCategories = productCategories;
    }

    /**
     * 대시보드 차트 응답 객체를 생성합니다.
     *
     * @param reviewRatingDistribution 리뷰 평점 분포
     * @param customerStatusDistribution 고객 상태 분포
     * @param productCategoryDistribution 상품 카테고리 분포
     * @return 대시보드 차트 응답 DTO
     */
    public static DashboardChartResponse of(
            List<ReviewRatingDistributionResponse> reviewRatingDistribution,
            List<CustomerStatusDistributionResponse> customerStatusDistribution,
            List<ProductCategoryDistributionResponse> productCategoryDistribution
    ) {
        return new DashboardChartResponse(
                reviewRatingDistribution,
                customerStatusDistribution,
                productCategoryDistribution
        );
    }
}
