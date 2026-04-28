package com.ildang100.backoffice.order.controller;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.order.dto.OrderCreateRequest;
import com.ildang100.backoffice.order.dto.OrderCreateResponse;
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
}
