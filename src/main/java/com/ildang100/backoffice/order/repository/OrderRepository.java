package com.ildang100.backoffice.order.repository;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.customer.dto.CustomerOrderStats;
import com.ildang100.backoffice.dashboard.dto.RecentOrderResponse;
import com.ildang100.backoffice.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 주문 상태와 검색어 조건에 맞는 주문 목록을 페이지 단위로 조회합니다.
     *
     * <p>{@code keyword}가 없으면 검색어 조건을 적용하지 않습니다.
     * 검색어가 있으면 고객 이름에 포함되거나 주문 번호와 일치하는 주문을 조회합니다.
     * 목록 응답 변환 시 추가 조회가 발생하지 않도록 고객, 상품, 관리자 연관 정보를 함께 조회합니다.</p>
     *
     * @param keyword 고객 이름 또는 주문 번호 검색어
     * @param orderNumber 주문 번호로 변환된 검색어. 숫자가 아닌 검색어면 {@code null}
     * @param status 조회할 주문 상태. {@code null}이면 상태 조건 없음
     * @param pageable 페이지 및 정렬 정보
     * @return 조건에 맞는 주문 페이지
     */
    @Query(
            value = """
                select o
                from Order o
                join fetch o.customer c
                join fetch o.product p
                left join fetch o.admin a
                where (:status is null or o.status = :status)
                  and (
                        :keyword is null
                        or :keyword = ''
                        or c.name like concat('%', :keyword, '%')
                        or (:orderNumber is not null and o.orderNumber = :orderNumber)
                  )
                """,
            countQuery = """
                select count(o)
                from Order o
                join o.customer c
                where (:status is null or o.status = :status)
                  and (
                        :keyword is null
                        or :keyword = ''
                        or c.name like concat('%', :keyword, '%')
                        or (:orderNumber is not null and o.orderNumber = :orderNumber)
                  )
                """
    )
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            @Param("orderNumber") Long orderNumber,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    /**
     * 고객 ID 목록에 해당하는 고객별 주문 통계를 조회합니다.
     *
     * <p>목록 조회 화면에서 현재 페이지에 포함된 고객들의 주문 통계를 한 번에 조회하기 위한 집계 쿼리입니다.</p>
     *
     * @param customerIds 조회할 고객 ID 목록
     * @param excludedStatus 집계에서 제외할 주문 상태
     * @return 고객별 주문 통계 목록
     */
    @Query("""
            select new com.ildang100.backoffice.customer.dto.CustomerOrderStats(
                o.customer.id,
                count(o),
                sum(o.totalPrice)
            )
            from Order o
            where o.customer.id in :customerIds
              and o.status <> :excludedStatus
            group by o.customer.id
            """)
    List<CustomerOrderStats> findCustomerOrderStatsByCustomerIds(
            @Param("customerIds") List<Long> customerIds,
            @Param("excludedStatus") OrderStatus excludedStatus
    );

    /**
     * 특정 고객의 주문 통계를 조회합니다.
     *
     * <p>고객 상세, 수정, 상태 변경 응답에 포함할 주문 통계를 조회합니다.</p>
     *
     * @param customerId 조회할 고객 ID
     * @param excludedStatus 집계에서 제외할 주문 상태
     * @return 고객 주문 통계
     */
    @Query("""
            select new com.ildang100.backoffice.customer.dto.CustomerOrderStats(
                o.customer.id,
                count(o),
                sum(o.totalPrice)
            )
            from Order o
            where o.customer.id = :customerId
              and o.status <> :excludedStatus
            group by o.customer.id
            """)
    Optional<CustomerOrderStats> findCustomerOrderStatsByCustomerId(
            @Param("customerId") Long customerId,
            @Param("excludedStatus") OrderStatus excludedStatus
    );

    /**
     * 대시보드에 표시할 최근 주문 목록을 조회합니다.
     *
     * <p>
     * 최근 생성된 주문을 기준으로 정렬하며,
     * 조회 개수는 {@link Pageable}을 통해 제한합니다.
     * </p>
     *
     * <p>
     * 주문번호, 고객명, 상품명, 주문 금액, 주문 상태만 조회하여
     * 대시보드 전용 응답 DTO로 직접 매핑합니다.
     * </p>
     *
     * @param pageable 조회 개수 및 페이징 정보
     * @return 최근 주문 응답 목록
     *
     * @author 이우람
     * @since 2026-04-29
     */
    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.customer c
        JOIN FETCH o.product p
        ORDER BY o.createdAt DESC
        """)
    List<Order> findRecentOrdersBySortByDesc(Pageable pageable);
}
