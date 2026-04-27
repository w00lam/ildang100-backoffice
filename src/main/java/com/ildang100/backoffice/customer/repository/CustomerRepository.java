package com.ildang100.backoffice.customer.repository;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.customer.entity.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
            select c
            from Customer c
            where (:status is null or c.status = :status)
              and (
                    :keyword is null
                    or :keyword = ''
                    or c.name like concat('%', :keyword, '%')
                    or c.email like concat('%', :keyword, '%')
              )
            """)
    /**
     * 고객 상태와 검색어 조건에 맞는 고객 목록을 페이지 단위로 조회합니다.
     *
     * <p>{@code keyword}가 {@code null} 또는 빈 문자열이면 검색어 조건을 적용하지 않습니다.
     * 검색어가 있으면 고객 이름 또는 이메일에 포함되는 고객을 조회합니다.</p>
     *
     * @param keyword 고객 이름 또는 이메일 검색어
     * @param status 조회할 고객 상태. {@code null}이면 상태 조건 없음
     * @param pageable 페이지 및 정렬 정보
     * @return 조건에 맞는 고객 페이지
     */
    Page<Customer> searchCustomers(
            @Param("keyword") String keyword,
            @Param("status") CustomerStatus status,
            Pageable pageable);

    /**
     * 고객 상태별 개수를 조회합니다.
     *
     * <p>
     * 대시보드 Summary 통계에서 활성 고객 수를 계산하기 위해 사용됩니다.
     * </p>
     *
     * @param status 고객 상태
     * @return 해당 상태의 고객 수
     *
     * @author 이우람
     * @since 2026-04-27
     */
    long countByStatus(CustomerStatus status);

    boolean existsByEmailAndIdNot(String email, Long customerId);
}
