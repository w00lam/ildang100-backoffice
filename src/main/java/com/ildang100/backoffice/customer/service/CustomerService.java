package com.ildang100.backoffice.customer.service;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.customer.dto.CustomerListResponse;
import com.ildang100.backoffice.customer.dto.CustomerResponse;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 고객 목록 조회 조건을 검증하고 페이지 요청 정보로 변환한 뒤 고객 목록을 조회합니다.
     *
     * <p>{@code page}는 1부터 시작하며, 내부 조회 시 Spring Data의 0 기반 페이지 번호로 변환합니다.</p>
     *
     * @param keyword 고객 이름 또는 이메일 검색어. {@code null} 또는 빈 문자열이면 검색 조건 없음
     * @param page 조회할 페이지 번호. 1 이상이어야 함
     * @param size 페이지당 조회할 고객 수. 1 이상이어야 함
     * @param sortBy 정렬 기준. 허용 값: {@code name}, {@code email}, {@code createdAt}
     * @param sortOrder 정렬 방향. 허용 값: {@code asc}, {@code desc}
     * @param status 조회할 고객 상태. {@code null}이면 상태 조건 없음
     * @return 고객 목록 응답 DTO
     * @throws ServiceException 페이지, 크기, 정렬 기준 또는 정렬 방향이 유효하지 않은 경우
     */
    @Transactional(readOnly = true)
    public CustomerListResponse getCustomers(
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortOrder,
            CustomerStatus status
    ) {
        if (page < 1 || size < 1) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }

        String sortProperty = convertSortProperty(sortBy);
        Sort.Direction direction = convertSortDirection(sortOrder);

        // API의 1 기반 페이지 번호를 Spring Data의 0 기반 페이지 번호로 변환한다.
        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(direction, sortProperty)
        );

        Page<Customer> customers = customerRepository.searchCustomers(
                keyword,
                status,
                pageable
        );

        return CustomerListResponse.from(customers);
    }

    /**
     * 고객 ID로 고객 상세 정보를 조회합니다.
     *
     * @param customerId 조회할 고객 ID
     * @return 고객 상세 응답 DTO
     * @throws ServiceException 고객을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        return CustomerResponse.from(customer);
    }

    private Sort.Direction convertSortDirection(String sortOrder) {
        if ("asc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.ASC;
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.DESC;
        }

        throw new ServiceException(ErrorCode.VALIDATION_FAILED);
    }

    private String convertSortProperty(String sortBy) {
        if ("name".equals(sortBy)) {
            return "name";
        }

        if ("email".equals(sortBy)) {
            return "email";
        }

        if ("createdAt".equals(sortBy)) {
            return "createdAt";
        }

        throw new ServiceException(ErrorCode.VALIDATION_FAILED);
    }
}
