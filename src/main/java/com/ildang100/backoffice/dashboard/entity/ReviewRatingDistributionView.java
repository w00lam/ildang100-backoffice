package com.ildang100.backoffice.dashboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

/**
 * 리뷰 평점 분포 View Entity입니다.
 *
 * <p>
 * 리뷰 평점별 개수를 조회하기 위한 DB View와 매핑됩니다.
 * 차트 데이터 표시를 위한 조회 전용 엔티티입니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
@Entity
@Immutable
@Table(name = "review_rating_distribution_view")
public class ReviewRatingDistributionView {

    /**
     * 리뷰 평점입니다.
     *
     * <p>
     * 평점 값은 1~5 범위를 기준으로 사용됩니다.
     * View 조회 결과에서 평점별 고유 값이 식별자 역할을 합니다.
     * </p>
     */
    @Id
    private Integer rating;

    /**
     * 해당 평점의 리뷰 개수입니다.
     */
    private Long reviewCount;
}
