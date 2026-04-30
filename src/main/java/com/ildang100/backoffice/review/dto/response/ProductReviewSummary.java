package com.ildang100.backoffice.review.dto.response;

import com.ildang100.backoffice.review.repository.projection.RatingCount;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 상품 상세용 리뷰 통계 응답 DTO (Story R-4 — Product P-8 소비).
 *
 * <p>
 * Review BC 도메인 모델 §외부 협력 관계 §통계 응답 계약 표가 단일 진실원천.
 * 표에 박제된 표현 형식 정책(반올림·0건 처리·누락 키 채움)을 본 DTO의 정적
 * 팩토리에 캡슐화 — 정책 변경 시 본 클래스 한 곳만 수정하면 됨.
 * </p>
 *
 * <p>
 * Service는 raw 집계 결과만 넘기며, 표현 형식 변환은 본 DTO가 자체 책임:
 * <ul>
 *     <li>평균: {@code BigDecimal.scale(1, HALF_UP)}으로 잠금</li>
 *     <li>별점별 분포: 1~5 누락 키를 0L로 채움</li>
 *     <li>0건: {@link #empty()}로 분리되어 의도가 명시적으로 드러남</li>
 * </ul>
 * </p>
 *
 * @author [작성자]
 * @since 2026-04-29
 */
@Getter
public class ProductReviewSummary {

    private static final BigDecimal ZERO_AVERAGE = BigDecimal.valueOf(0.0).setScale(1, RoundingMode.HALF_UP);
    private static final Map<Integer, Long> ZERO_DISTRIBUTION = Map.of(
            1, 0L, 2, 0L, 3, 0L, 4, 0L, 5, 0L
                                                                      );

    private final BigDecimal averageRating;
    private final long totalCount;
    private final Map<Integer, Long> ratingDistribution;
    private final List<LatestReviewItem> latestReviews;

    private ProductReviewSummary(
            BigDecimal averageRating,
            long totalCount,
            Map<Integer, Long> ratingDistribution,
            List<LatestReviewItem> latestReviews
                                ) {
        this.averageRating = averageRating;
        this.totalCount = totalCount;
        this.ratingDistribution = ratingDistribution;
        this.latestReviews = latestReviews;
    }

    /**
     * 리뷰 0건일 때의 통계 응답을 반환합니다.
     *
     * <p>
     * 통계 응답 계약 표 §0건일 때 값 컬럼 그대로: - 0건일 때 빨리 처리 가능해짐
     * 평균 0.0, 개수 0, 별점별 분포 1~5 키 모두 0, 최신 빈 배열.
     * </p>
     */
    public static ProductReviewSummary empty() {
        return new ProductReviewSummary(
                ZERO_AVERAGE,
                0L,
                ZERO_DISTRIBUTION,
                Collections.emptyList()
        );
    }

    /**
     * Repository raw 집계 결과를 응답 계약 형식으로 잠가 반환합니다.
     *
     * <p>
     * 캡슐화된 표현 형식 정책:
     * <ul>
     *     <li>{@code rawAverage}를 {@code BigDecimal.scale(1, HALF_UP)}으로 잠금</li>
     *     <li>별점별 분포의 1~5 누락 키를 0L로 채움</li>
     * </ul>
     * </p>
     *
     * <p>
     * 호출자(Service)는 0건 분기를 먼저 처리하고 본 메서드를 호출하므로,
     * {@code rawAverage}는 본 메서드 진입 시점에 절대 {@code null}이 아닙니다.
     * </p>
     */
    public static ProductReviewSummary of(
            Double rawAverage,
            long totalCount,
            List<RatingCount> rawDistribution,
            List<LatestReviewItem> latestReviews
                                         ) {
        BigDecimal averageRating = BigDecimal.valueOf(rawAverage)
                                             .setScale(1, RoundingMode.HALF_UP);
        Map<Integer, Long> distribution = fillMissingRatingKeys(rawDistribution);

        return new ProductReviewSummary(averageRating, totalCount, distribution, latestReviews);
    }

    /**
     * 별점별 분포 결과의 누락 키(0건인 별점)를 0L로 채워 1~5 모든 키를 보장합니다.
     * 통계 응답 계약 §누락 키 처리 정책 직접 매핑.
     */
    private static Map<Integer, Long> fillMissingRatingKeys(List<RatingCount> rawDistribution) {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int rating = 1; rating <= 5; rating++) {
            distribution.put(rating, 0L);
        }
        for (RatingCount row : rawDistribution) {
            distribution.put(row.rating(), row.count());
        }
        return distribution;
    }
}