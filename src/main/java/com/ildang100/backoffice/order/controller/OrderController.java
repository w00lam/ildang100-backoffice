package com.ildang100.backoffice.order.controller;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.order.dto.OrderCreateRequest;
import com.ildang100.backoffice.order.dto.OrderCreateResponse;
import com.ildang100.backoffice.order.dto.OrderDetailResponse;
import com.ildang100.backoffice.order.dto.OrderListResponse;
import com.ildang100.backoffice.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문을 생성합니다.
     *
     * <p>로그인한 관리자를 주문 담당자로 기록하고, 요청한 상품의 재고를 차감한 뒤 주문을 생성합니다.</p>
     *
     * @param request 주문 생성 요청 정보
     * @param session 로그인 관리자 확인을 위한 HTTP 세션
     * @return 생성된 주문 정보를 포함한 응답
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonApiResponse<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            HttpSession session
    ){
        LoginAdminDto loginAdmin = SessionUtils.getLoginAdmin(session);

        OrderCreateResponse response = orderService.createOrder(loginAdmin.getId(), request);

        return CommonApiResponse.success(
                HttpStatus.CREATED,
                "주문 생성 성공",
                response
        );
    }

    /**
     * 주문 목록을 조회합니다.
     *
     * <p>검색어, 주문 상태, 페이지, 정렬 조건을 기준으로 주문 목록을 조회합니다.
     * 검색어는 고객 이름과 주문 번호를 대상으로 적용됩니다.</p>
     *
     * @param keyword 고객 이름 또는 주문 번호 검색어. 생략 시 검색 조건 없이 조회
     * @param page 조회할 페이지 번호. 1부터 시작
     * @param size 페이지당 조회할 주문 수
     * @param sortBy 정렬 기준. 허용 값: {@code quantity}, {@code totalPrice}, {@code createdAt}
     * @param sortOrder 정렬 방향. 허용 값: {@code asc}, {@code desc}
     * @param status 조회할 주문 상태. 생략 시 전체 상태 조회
     * @param session 로그인 관리자 확인을 위한 HTTP 세션
     * @return 주문 목록과 페이지 정보를 포함한 응답
     */
    @GetMapping
    public CommonApiResponse<OrderListResponse> getOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) OrderStatus status,
            HttpSession session
    ) {
        SessionUtils.getLoginAdmin(session);

        OrderListResponse response = orderService.getOrders(
                keyword,
                page,
                size,
                sortBy,
                sortOrder,
                status
        );

        return CommonApiResponse.success(
                HttpStatus.OK,
                "주문 리스트 조회 성공",
                response
        );
    }

    /**
     * 주문 상세 정보를 조회합니다.
     *
     * @param orderId 조회할 주문 ID
     * @param session 로그인 관리자 확인을 위한 HTTP 세션
     * @return 주문 상세 정보를 포함한 응답
     */
    @GetMapping("/{orderId}")
    public CommonApiResponse<OrderDetailResponse> getOrder(
            @PathVariable Long orderId,
            HttpSession session
    ) {
        SessionUtils.getLoginAdmin(session);

        OrderDetailResponse response = orderService.getOrder(orderId);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "주문 상세 조회 성공",
                response
        );
    }
}
