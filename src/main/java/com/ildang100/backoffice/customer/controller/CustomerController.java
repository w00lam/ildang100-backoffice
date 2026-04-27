package com.ildang100.backoffice.customer.controller;

import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.enums.CustomerStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.customer.dto.CustomerListResponse;
import com.ildang100.backoffice.customer.dto.CustomerResponse;
import com.ildang100.backoffice.customer.dto.CustomerStatusUpdateRequest;
import com.ildang100.backoffice.customer.dto.CustomerUpdateRequest;
import com.ildang100.backoffice.customer.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/customers")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 고객 목록을 조회합니다.
     *
     * <p>검색어, 고객 상태, 페이지, 정렬 조건을 기준으로 고객 목록을 조회합니다.
     * {@code keyword}는 고객 이름과 이메일을 대상으로 검색합니다.</p>
     *
     * @param keyword 고객 이름 또는 이메일 검색어. 생략 시 검색 조건 없이 조회
     * @param page 조회할 페이지 번호. 1부터 시작
     * @param size 페이지당 조회할 고객 수
     * @param sortBy 정렬 기준. 허용 값: {@code name}, {@code email}, {@code createdAt}
     * @param sortOrder 정렬 방향. 허용 값: {@code asc}, {@code desc}
     * @param status 조회할 고객 상태. 생략 시 전체 상태 조회
     * @param session 로그인 관리자 확인을 위한 HTTP 세션
     * @return 고객 목록과 페이지 정보를 포함한 응답
     */
    @GetMapping
    public CommonApiResponse<CustomerListResponse> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) CustomerStatus status,
            HttpSession session
    ){
        SessionUtils.getLoginAdmin(session);

        CustomerListResponse response = customerService.getCustomers(
                keyword,
                page,
                size,
                sortBy,
                sortOrder,
                status
        );

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 리스트 조회 성공",
                response
        );
    }

    /**
     * 고객 상세 정보를 조회합니다.
     *
     * @param customerId 조회할 고객 ID
     * @param session 로그인 관리자 확인을 위한 HTTP 세션
     * @return 고객 상세 정보를 포함한 응답
     */
    @GetMapping("/{customerId}")
    CommonApiResponse<CustomerResponse> getCustomer(
          @PathVariable Long customerId,
         HttpSession session
    ) {
        SessionUtils.getLoginAdmin(session);

        CustomerResponse response = customerService.getCustomer(customerId);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 상세 조회 성공",
                response
        );
    }

    /**
     * 고객 기본 정보를 수정합니다.
     *
     * @param customerId 정보를 수정할 고객 ID
     * @param request 변경할 고객 이름, 이메일, 전화번호 정보
     * @param session 로그인 관리자 확인을 위한 HTTP 세션
     * @return 변경된 고객 정보를 포함한 응답
     */
    @PutMapping("/{customerId}")
    CommonApiResponse<CustomerResponse> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request,
            HttpSession session
    ) {
        SessionUtils.getLoginAdmin(session);

        CustomerResponse response = customerService.updateCustomer(customerId, request);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 정보 수정 완료",
                response
        );
    }

    /**
     * 고객 상태를 수정합니다.
     *
     * @param customerId 상태를 수정할 고객 ID
     * @param request 변경할 고객 상태 정보
     * @param session 로그인 관리자 확인을 위한 HTTP 세션
     * @return 변경된 고객 정보를 포함한 응답
     */
    @PutMapping("/{customerId}/status")
    public CommonApiResponse<CustomerResponse> updateCustomerStatus(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerStatusUpdateRequest request,
            HttpSession session
    ) {
        SessionUtils.getLoginAdmin(session);

        CustomerResponse response = customerService.updateCustomerStatus(customerId, request);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 상태 수정 완료",
                response
        );
    }

    @DeleteMapping("/{customerId}")
    public CommonApiResponse<Void> deleteCustomer(
            @PathVariable Long customerId,
            HttpSession session
    ) {
        SessionUtils.getLoginAdmin(session);

        customerService.deleteCustomer(customerId);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "고객 삭제 완료",
                null
        );
    }
}
