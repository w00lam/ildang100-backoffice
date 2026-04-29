package com.ildang100.backoffice.product.repository;

import com.ildang100.backoffice.common.enums.ProductStatus;
import com.ildang100.backoffice.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 상품 동적 검색.
     *
     * <p>
     * 키워드(상품명 부분 일치)와 상태(정확 일치) 조건을 동적으로 조합한다.
     * 등록 관리자(admin)를 fetch join으로 함께 로딩하여 N+1 문제를 회피한다.
     * </p>
     *
     * <p>
     * 소프트 삭제({@code deletionStatus = DELETED})된 상품은 결과에서 자동 제외된다 (Story P-6).
     * </p>
     *
     * <p>
     * count 쿼리는 별도로 분리하며, 페이지 메타 정보 계산용이라 fetch join을 제거한다
     * (Hibernate에서 fetch join + count 조합 시 발생할 수 있는 경고를 회피).
     * </p>
     *
     * @param keyword  상품명 키워드. {@code null}이거나 빈 문자열이면 미적용
     * @param status   판매 상태 필터. {@code null}이면 미적용
     * @param pageable 페이징·정렬 정보 (0-based)
     */
    @Query(
            value = "SELECT p FROM Product p JOIN FETCH p.admin a "
                    + "WHERE p.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED "
                    + "AND (:keyword IS NULL OR :keyword = '' OR p.name LIKE CONCAT('%', :keyword, '%')) "
                    + "AND (:category IS NULL OR :category = '' OR p.category = :category) "
                    + "AND (:status IS NULL OR p.status = :status)",
            countQuery = "SELECT COUNT(p) FROM Product p "
                    + "WHERE p.deletionStatus = com.ildang100.backoffice.common.enums.DeletionStatus.NOT_DELETED "
                    + "AND (:keyword IS NULL OR :keyword = '' OR p.name LIKE CONCAT('%', :keyword, '%')) "
                    + "AND (:category IS NULL OR :category = '' OR p.category = :category) "
                    + "AND (:status IS NULL OR p.status = :status)"
    )
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("status") ProductStatus status,
            Pageable pageable
                                );

    /**
     * 상품 상세 조회 (단건).
     *
     * <p>
     * 등록 관리자(admin)를 fetch join으로 함께 로딩하여 단일 쿼리로 N+1 문제를 회피한다.
     * 응답({@link com.ildang100.backoffice.product.dto.response.ProductDetailResponse})에
     * 등록 관리자의 이름·이메일이 포함되므로 fetch join이 필수다.
     * </p>
     *
     * @param productId 조회할 상품 ID
     * @return 상품 (없으면 {@code Optional.empty()})
     */
    @Query("SELECT p FROM Product p JOIN FETCH p.admin a WHERE p.id = :productId")
    Optional<Product> findDetailById(@Param("productId") Long productId);

}