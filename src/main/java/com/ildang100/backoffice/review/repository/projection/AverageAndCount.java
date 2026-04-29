package com.ildang100.backoffice.review.repository.projection;

/**
 * R-4 평균/카운트 집계 결과 Projection.
 *
 * <p>
 * 리뷰 0건일 때 {@code average}는 {@code null}로 반환되므로 Wrapper 타입 사용.
 * Service에서 {@code count == 0L} 분기 처리 후 0건이 아닐 때만 평균을 사용.
 * </p>
 */
public record AverageAndCount(Double average, Long count) {
}
