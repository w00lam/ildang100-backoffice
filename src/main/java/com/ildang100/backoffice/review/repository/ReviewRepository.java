package com.ildang100.backoffice.review.repository;

import com.ildang100.backoffice.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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



}
