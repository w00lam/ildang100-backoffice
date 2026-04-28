package com.ildang100.backoffice.order.repository;

import com.ildang100.backoffice.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {


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
