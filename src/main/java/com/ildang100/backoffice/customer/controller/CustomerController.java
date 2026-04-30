package com.ildang100.backoffice.customer.controller;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.customer.dto.CustomerListResponse;
import com.ildang100.backoffice.customer.dto.CustomerResponse;
import com.ildang100.backoffice.customer.dto.CustomerStatusUpdateRequest;
import com.ildang100.backoffice.customer.dto.CustomerUpdateRequest;
import com.ildang100.backoffice.customer.policy.CustomerSortPolicy;
import com.ildang100.backoffice.customer.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 고객 목록을 조회합니다.
     */
    @GetMapping
    public CommonApiResponse<CustomerListResponse> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) CustomerStatus status
    ) {
        Sort sort = CustomerSortPolicy.resolve(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        CustomerListResponse response = customerService.getCustomers(
                keyword,
                status,
                pageable
        );

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 리스트 조회 성공",
                response
        );
    }

    /**
     * 고객 상세 정보를 조회합니다.
     */
    @GetMapping("/{customerId}")
    CommonApiResponse<CustomerResponse> getCustomer(
            @PathVariable Long customerId
    ) {
        CustomerResponse response = customerService.getCustomer(customerId);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 상세 조회 성공",
                response
        );
    }

    /**
     * 고객 기본 정보를 수정합니다.
     */
    @PutMapping("/{customerId}")
    CommonApiResponse<CustomerResponse> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        CustomerResponse response = customerService.updateCustomer(customerId, request);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 정보 수정 완료",
                response
        );
    }

    /**
     * 고객 상태를 수정합니다.
     */
    @PutMapping("/{customerId}/status")
    public CommonApiResponse<CustomerResponse> updateCustomerStatus(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerStatusUpdateRequest request
    ) {
        CustomerResponse response = customerService.updateCustomerStatus(customerId, request);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 상태 수정 완료",
                response
        );
    }

    /**
     * 고객을 소프트 삭제 처리합니다.
     *
     * <p>실제 데이터를 삭제하지 않고 {@code deletionStatus}를 {@code DELETED}로 변경합니다.</p>
     */
    @DeleteMapping("/{customerId}")
    public CommonApiResponse<Void> deleteCustomer(
            @PathVariable Long customerId
    ) {
        customerService.deleteCustomer(customerId);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 삭제 완료",
                null
        );
    }
}
