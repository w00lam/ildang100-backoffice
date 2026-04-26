package com.ildang100.backoffice.customer.service;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.customer.dto.CustomerListResponse;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 고객 상태 조건에 따라 고객 목록을 페이지 단위로 조회합니다.
     *
     * @param status 조회할 고객 상태. {@code null}이면 상태 조건 없이 조회
     * @param pageable 페이지 요청 정보
     * @return 고객 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public CustomerListResponse getCustomers(CustomerStatus status, Pageable pageable) {
        Page<Customer> customers;

        if (status == null) {
            customers = customerRepository.findAll(pageable);
        } else {
            customers = customerRepository.findByStatus(status, pageable);
        }

        return CustomerListResponse.from(customers);
    }
}
