package com.ildang100.backoffice.customer.controller;

import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.customer.dto.CustomerListResponse;
import com.ildang100.backoffice.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/customers")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 고객 목록을 조회합니다.
     *
     * <p>{@code status}가 없으면 전체 고객을 조회하고, 값이 있으면 해당 상태의 고객만 조회합니다.</p>
     *
     * @param status 조회할 고객 상태. 생략 시 전체 조회
     * @param pageable 페이지 번호, 크기, 정렬 정보
     * @return 고객 목록과 페이지 정보를 포함한 응답
     */
    @GetMapping
    public CommonApiResponse<CustomerListResponse> getCustomers(
            @RequestParam(required = false) CustomerStatus status,
            @PageableDefault(size = 10) Pageable pageable
    ){
        CustomerListResponse response = customerService.getCustomers(status, pageable);
        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 리스트 조회 성공",
                response
        );
    }
}
