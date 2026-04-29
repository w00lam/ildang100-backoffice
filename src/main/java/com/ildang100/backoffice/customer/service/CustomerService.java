package com.ildang100.backoffice.customer.service;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.customer.dto.CustomerListResponse;
import com.ildang100.backoffice.customer.dto.CustomerOrderStats;
import com.ildang100.backoffice.customer.dto.CustomerResponse;
import com.ildang100.backoffice.customer.dto.CustomerStatusUpdateRequest;
import com.ildang100.backoffice.customer.dto.CustomerUpdateRequest;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.customer.repository.CustomerRepository;
import com.ildang100.backoffice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    /**
     * 고객 목록을 페이지 단위로 조회합니다.
     *
     * <p>검색어와 고객 상태 조건을 적용하고, 조회된 고객별 주문 통계를 함께 포함합니다.</p>
     *
     * @param keyword 고객 이름 또는 이메일 검색어. {@code null} 또는 빈 문자열이면 검색 조건 없음
     * @param status 조회할 고객 상태. {@code null}이면 상태 조건 없음
     * @param pageable 페이지 및 정렬 정보
     * @return 고객 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public CustomerListResponse getCustomers(
            String keyword,
            CustomerStatus status,
            Pageable pageable
    ) {
        Page<Customer> customers = customerRepository.searchCustomers(
                keyword,
                status,
                pageable
        );

        Map<Long, CustomerOrderStats> orderStatsMap = findCustomerOrderStatsMap(customers);

        return CustomerListResponse.from(customers, orderStatsMap);
    }

    /**
     * 현재 고객 목록 페이지에 포함된 고객들의 주문 통계를 조회해 고객 ID 기준 Map으로 변환합니다.
     *
     * <p>목록 응답 생성 시 고객별 주문 통계를 반복 조회하지 않고, 한 번에 조회한 결과를
     * {@code customerId -> 주문 통계} 형태로 만들어 빠르게 매칭하기 위해 사용합니다.</p>
     *
     * @param customers 현재 조회된 고객 페이지
     * @return 고객 ID를 key로 하는 주문 통계 Map
     */
    private Map<Long, CustomerOrderStats> findCustomerOrderStatsMap(Page<Customer> customers) {
        // 목록 응답에 실제로 포함될 고객 ID만 추출한다.
        List<Long> customerIds = customers.getContent().stream()
                .map(Customer::getId)
                .toList();

        if (customerIds.isEmpty()) {
            return Map.of();
        }

        List<CustomerOrderStats> orderStatsList = orderRepository.findCustomerOrderStatsByCustomerIds(
                customerIds,
                OrderStatus.CANCELLED
        );

        // CustomerResponse 생성 시 고객 ID로 바로 찾을 수 있도록 List 결과를 Map으로 재구성한다.
        Map<Long, CustomerOrderStats> orderStatsMap = new HashMap<>();

        for (CustomerOrderStats orderStats : orderStatsList) {
            orderStatsMap.put(orderStats.getCustomerId(), orderStats);
        }

        return orderStatsMap;
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
        Customer customer = getCustomerOrThrow(customerId);
        return toCustomerResponse(customer);
    }

    /**
     * 고객 기본 정보를 수정합니다.
     *
     * <p>수정 가능한 필드는 이름, 이메일, 전화번호입니다.
     * 이메일을 변경하는 경우 다른 고객이 사용 중인 이메일인지 검증합니다.</p>
     *
     * @param customerId 정보를 수정할 고객 ID
     * @param request 변경할 고객 이름, 이메일, 전화번호 정보
     * @return 변경된 고객 상세 응답 DTO
     * @throws ServiceException 고객 ID가 유효하지 않거나, 고객을 찾을 수 없거나, 이메일이 중복된 경우
     */
    @Transactional
    public CustomerResponse updateCustomer(
            Long customerId,
            CustomerUpdateRequest request
    ) {
        Customer customer = getCustomerOrThrow(customerId);

        validateDuplicateEmail(request.getEmail(), customerId);

        customer.updateInfo(
                request.getName(),
                request.getEmail(),
                request.getTele()
        );

        return toCustomerResponse(customer);
    }

    private void validateDuplicateEmail(String email, Long customerId) {
        if (email != null && customerRepository.existsByEmailAndIdNot(email, customerId)) {
            throw new ServiceException(ErrorCode.EMAIL_DUPLICATE);
        }
    }

    /**
     * 고객 상태를 수정합니다.
     *
     * @param customerId 상태를 수정할 고객 ID
     * @param request 변경할 고객 상태 정보
     * @return 변경된 고객 상세 응답 DTO
     * @throws ServiceException 고객 ID가 유효하지 않거나 고객을 찾을 수 없는 경우
     */
    @Transactional
    public CustomerResponse updateCustomerStatus(
            Long customerId,
            CustomerStatusUpdateRequest request
    ) {
        Customer customer = getCustomerOrThrow(customerId);

        customer.updateStatus(request.getStatus());

        return toCustomerResponse(customer);
    }

    /**
     * 고객을 삭제 처리합니다.
     *
     * <p>고객 데이터를 물리 삭제하지 않고 비활성 상태로 변경합니다.</p>
     *
     * @param customerId 삭제 처리할 고객 ID
     * @throws ServiceException 고객 ID가 유효하지 않거나, 고객을 찾을 수 없거나, 이미 비활성 상태인 경우
     */
    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer customer = getCustomerOrThrow(customerId);

        customer.withdraw();
    }

    /**
     * 고객 ID로 고객을 조회하고, 없으면 예외를 발생시킵니다.
     *
     * @param customerId 조회할 고객 ID
     * @return 조회된 고객 엔티티
     * @throws ServiceException 고객 ID가 유효하지 않거나 고객을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Customer getCustomerOrThrow(Long customerId) {
        validateCustomerId(customerId);

        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    private void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }

    private CustomerOrderStats findCustomerOrderStats(Long customerId) {
        return orderRepository.findCustomerOrderStatsByCustomerId(
                        customerId,
                        OrderStatus.CANCELLED
                )
                .orElse(CustomerOrderStats.empty(customerId));
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        CustomerOrderStats orderStats = findCustomerOrderStats(customer.getId());
        return CustomerResponse.from(customer, orderStats);
    }
}
