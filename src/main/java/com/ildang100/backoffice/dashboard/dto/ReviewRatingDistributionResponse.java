package com.ildang100.backoffice.dashboard.dto;

import com.ildang100.backoffice.dashboard.entity.ReviewRatingDistributionView;
import lombok.Getter;

/**
 * 리뷰 평점 분포 응답 DTO입니다.
 *
 * <p>
 * 별점별 리뷰 개수를 차트로 표시하기 위해 사용됩니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
public class ReviewRatingDistributionResponse {

    /**
     * 리뷰 평점입니다.
     */
    private final Integer rating;

    /**
     * 해당 평점의 리뷰 개수입니다.
     */
    private final Long reviewCount;

    private ReviewRatingDistributionResponse(Integer rating, Long reviewCount) {
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    /**
     * View Entity를 응답 DTO로 변환합니다.
     *
     * @param view 리뷰 평점 분포 View Entity
     * @return 리뷰 평점 분포 응답 DTO
     */
    public static ReviewRatingDistributionResponse from(ReviewRatingDistributionView view) {
        return new ReviewRatingDistributionResponse(
                view.getRating(),
                view.getReviewCount()
        );
    }
}
