package com.ildang100.backoffice.order.repository;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 주문 상태와 검색어 조건에 맞는 주문 목록을 페이지 단위로 조회합니다.
     *
     * <p>{@code keyword}가 없으면 검색어 조건을 적용하지 않습니다.
     * 검색어가 있으면 고객 이름에 포함되거나 주문 번호와 일치하는 주문을 조회합니다.</p>
     *
     * @param keyword 고객 이름 또는 주문 번호 검색어
     * @param orderNumber 주문 번호로 변환된 검색어. 숫자가 아닌 검색어면 {@code null}
     * @param status 조회할 주문 상태. {@code null}이면 상태 조건 없음
     * @param pageable 페이지 및 정렬 정보
     * @return 조건에 맞는 주문 페이지
     */
    @Query("""
        select o
        from Order o
        join o.customer c
        join o.product p
        left join o.admin a
        where (:status is null or o.status = :status)
          and (
                :keyword is null
                or :keyword = ''
                or c.name like concat('%', :keyword, '%')
                or (:orderNumber is not null and o.orderNumber = :orderNumber)
          )
        """)
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            @Param("orderNumber") Long orderNumber,
            @Param("status") OrderStatus status,
            Pageable pageable
    );


    /**
     * 오늘 생성된 주문 수를 조회합니다.
     *
     * <p>
     * 주문 생성일(createdAt)의 날짜 부분만 추출하여
     * 현재 날짜(CURRENT_DATE)와 일치하는 주문을 집계합니다.
     * </p>
     *
     * <p><b>주의사항</b></p>
     * <ul>
     *     <li>DB 함수 DATE()를 사용하므로 데이터베이스에 종속될 수 있습니다.</li>
     *     <li>인덱스를 활용하지 못해 성능 저하가 발생할 수 있습니다.</li>
     *     <li>대량 데이터 환경에서는 시간 범위 조건(>=, &lt;) 방식이 더 권장됩니다.</li>
     * </ul>
     *
     * @return 오늘 생성된 주문 수
     *
     * @author 이우람
     * @since 2026-04-28
     */
    @Query("""
    SELECT COUNT(o)
    FROM Order o
    WHERE DATE(o.createdAt) = CURRENT_DATE
    """)
    long countTodayOrders();
}
