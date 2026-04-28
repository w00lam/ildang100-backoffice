package com.ildang100.backoffice.order.repository;

import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

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
}
