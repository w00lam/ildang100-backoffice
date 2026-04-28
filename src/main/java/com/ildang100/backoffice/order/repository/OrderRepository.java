package com.ildang100.backoffice.order.repository;

import com.ildang100.backoffice.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
