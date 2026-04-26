package com.ildang100.backoffice.customer.repository;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);
}
