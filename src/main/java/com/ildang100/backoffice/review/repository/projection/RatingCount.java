package com.ildang100.backoffice.review.repository.projection;

/**
 * R-4 별점별 개수 집계 결과 Projection.- 일부분만 미리 뽑아서 가져가는
 *
 * <p>
 * JPQL {@code SELECT new ...RatingCount(r.rating, COUNT(r))}로 매핑되어
 * {@code Object[]} 캐스팅 없이 타입 안전하게 Service까지 전달됩니다.
 * </p>
 */
public record RatingCount(Integer rating, Long count) {
}
