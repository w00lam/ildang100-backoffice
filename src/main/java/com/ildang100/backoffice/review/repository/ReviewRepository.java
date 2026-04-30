package com.ildang100.backoffice.review.repository;

import com.ildang100.backoffice.review.entity.Review;
import com.ildang100.backoffice.review.repository.projection.AverageAndCount;
import com.ildang100.backoffice.review.repository.projection.RatingCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 리뷰 동적 검색 (Story R-1).
     *
     * <p>
     * 특정 상품(productId)에 대한 리뷰 목록을 키워드(고객명·상품명 부분 일치) /
     * 평점(정확 일치) / 페이징·정렬 조건으로 조회한다. 작성 고객(customer),
     * 대상 상품(product), 연결 주문(order)을 fetch join으로 함께 로딩하여 N+1 문제를 회피한다.
     * </p>
     *
     * <p>
     * 소프트 삭제({@code deletionStatus = DELETED})된 리뷰는 결과에서 자동 제외된다 (Story R-3).
     * </p>
     *
     * <p>
     * count 쿼리는 별도로 분리하며, fetch join을 제거한다
     * 참조하므로 fetch 없이 일반 join을 사용한다.
     * </p>
     *
     * @param productId 대상 상품 ID (필수)
     * @param keyword   고객명/상품명 부분 일치 키워드. {@code null}이거나 빈 문자열이면 미적용
     * @param rating    평점 필터 (1~5). {@code null}이면 미적용
     * @param pageable  페이징·정렬 정보 (0-based)
     */
    @Query(
            value = "SELECT r FROM Review r "
                    + "JOIN FETCH r.customer c "
                    + "JOIN FETCH r.product p "
                    + "JOIN FETCH r.order o "
                    + "WHERE p.id = :productId "
                    + "AND r.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED "
                    + "AND (:keyword IS NULL OR :keyword = '' "
                    + "     OR c.name LIKE CONCAT('%', :keyword, '%') "
                    + "     OR p.name LIKE CONCAT('%', :keyword, '%')) "
                    + "AND (:rating IS NULL OR r.rating = :rating)",
            countQuery = "SELECT COUNT(r) FROM Review r "
                    + "JOIN r.customer c "
                    + "JOIN r.product p "
                    + "WHERE p.id = :productId "
                    + "AND r.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED "
                    + "AND (:keyword IS NULL OR :keyword = '' "
                    + "     OR c.name LIKE CONCAT('%', :keyword, '%') "
                    + "     OR p.name LIKE CONCAT('%', :keyword, '%')) "
                    + "AND (:rating IS NULL OR r.rating = :rating)"
    )
    Page<Review> searchReviews(
            @Param("productId") Long productId,
            @Param("keyword") String keyword,
            @Param("rating") Integer rating,
            Pageable pageable
                              );

    /**
     * 리뷰 상세 단건 조회 (Story R-2).
     *
     * <p>
     * 특정 상품(productId)에 속한 특정 리뷰(reviewId)를 조회합니다.
     * 작성 고객(customer), 대상 상품(product)을 fetch join으로 함께 로딩하여 N+1을 회피합니다.
     * 연결 주문(order)은 상세 응답에서 노출되지 않으므로 fetch join 대상에서 제외됩니다.
     * </p>
     *
     * <p>
     * 다음 케이스 모두 빈 {@link java.util.Optional}을 반환합니다 (Service에서
     * REVIEW_NOT_FOUND로 통일 처리):
     * <ul>
     *     <li>존재하지 않는 reviewId</li>
     *     <li>소프트 삭제({@code deletionStatus = DELETED})된 리뷰</li>
     *     <li>리뷰가 다른 productId에 속한 경우 (URL 경로 정합성 검증)</li>
     * </ul>
     * </p>
     *
     * @param productId 대상 상품 ID
     * @param reviewId  조회할 리뷰 ID
     */
    @Query(
            "SELECT r FROM Review r "
                    + "JOIN FETCH r.customer c "
                    + "JOIN FETCH r.product p "
                    + "WHERE r.id = :reviewId "
                    + "AND p.id = :productId "
                    + "AND r.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED"
    )
    Optional<Review> findDetailByProductIdAndId(
            @Param("productId") Long productId,
            @Param("reviewId") Long reviewId
                                               );

    /**
     * 리뷰 삭제용 단건 조회 (Story R-3).
     *
     * @param productId 대상 상품 ID
     * @param reviewId  조회할 리뷰 ID
     */
    @Query(
            "SELECT r FROM Review r "
                    + "WHERE r.id = :reviewId "
                    + "AND r.product.id = :productId"
    )
    Optional<Review> findByProductIdAndId(
            @Param("productId") Long productId,
            @Param("reviewId") Long reviewId
                                         );
    /**
     * 상품 평균 평점 + 총 개수 집계 (Story R-4).
     *
     * <p>
     * DELETED 리뷰는 집계에서 자동 제외. 리뷰 0건이면 {@code AverageAndCount(null, 0L)}.
     * Service에서 {@code count == 0L} 분기 후 처리.
     * </p>
     */
    @Query(
            "SELECT new com.ildang100.backoffice.review.repository.projection.AverageAndCount(AVG(r.rating), COUNT(r)) "
                    + "FROM Review r "
                    + "WHERE r.product.id = :productId "
                    + "AND r.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED"
    )
    AverageAndCount findAverageAndCountByProductId(@Param("productId") Long productId);

    /**
     * 상품 별점별 개수 집계 (Story R-4).
     *
     * <p>
     * DELETED 제외. 0건인 별점은 결과 row에 포함되지 않으므로 호출자
     * ({@link com.ildang100.backoffice.review.dto.response.ProductReviewSummary#of})가
     * 1~5 모든 키를 0으로 채우는 후처리 수행.
     * </p>
     */
    @Query(
            "SELECT new com.ildang100.backoffice.review.repository.projection.RatingCount(r.rating, COUNT(r)) "
                    + "FROM Review r "
                    + "WHERE r.product.id = :productId "
                    + "AND r.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED "
                    + "GROUP BY r.rating"
    )
    List<RatingCount> countByProductIdGroupByRating(@Param("productId") Long productId);

    /**
     * 상품 최신 리뷰 N건 조회 (Story R-4).
     *
     * <p>
     * {@code customer}를 fetch join으로 함께 로딩 — {@code LatestReviewItem.from} 변환 시
     * N+1 회피. 정렬·제한은 {@link Pageable}로 위임. DELETED 제외.
     * </p>
     *
     * @param pageable {@code PageRequest.of(0, latestLimit, Sort.by(DESC, "createdAt"))} 형태로 전달
     */
    @Query(
            "SELECT r FROM Review r "
                    + "JOIN FETCH r.customer "
                    + "WHERE r.product.id = :productId "
                    + "AND r.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED"
    )
    List<Review> findLatestByProductId(
            @Param("productId") Long productId,
            Pageable pageable
                                      );


}
