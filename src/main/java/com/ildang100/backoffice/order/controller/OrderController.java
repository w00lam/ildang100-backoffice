package com.ildang100.backoffice.order.controller;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.AuthUtils;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.order.dto.request.OrderCancelRequest;
import com.ildang100.backoffice.order.dto.request.OrderCreateRequest;
import com.ildang100.backoffice.order.dto.request.OrderStatusUpdateRequest;
import com.ildang100.backoffice.order.dto.response.*;
import com.ildang100.backoffice.order.policy.OrderSortPolicy;
import com.ildang100.backoffice.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문을 생성합니다.
     *
     * <p>JWT 인증 후 SecurityContext에 저장된 관리자 ID를 주문 담당자로 기록합니다.</p>
     */
    @PostMapping
    public CommonApiResponse<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {
        LoginAdminDto loginAdmin = AuthUtils.getLoginAdmin();

        OrderCreateResponse response = orderService.createOrder(loginAdmin.getId(), request);

        return CommonApiResponse.success(HttpStatus.CREATED, "주문 생성 성공", response);
    }

    /**
     * 주문 목록을 조회합니다.
     */
    @GetMapping
    public CommonApiResponse<OrderListResponse> getOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) OrderStatus status
    ) {
        Sort sort = OrderSortPolicy.resolve(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        OrderListResponse response = orderService.getOrders(keyword, status, pageable);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "주문 리스트 조회 성공",
                response
        );
    }

    /**
     * 주문 상세 정보를 조회합니다.
     */
    @GetMapping("/{orderId}")
    public CommonApiResponse<OrderDetailResponse> getOrder(
            @PathVariable Long orderId
    ) {
        OrderDetailResponse response = orderService.getOrder(orderId);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "주문 상세 조회 성공",
                response
        );
    }

    /**
     * 주문 상태를 수정합니다.
     */
    @PutMapping("/{orderId}/status")
    public CommonApiResponse<OrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        OrderStatusUpdateResponse response = orderService.updateOrderStatus(orderId, request);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "주문 상태 수정 완료",
                response
        );
    }

    /**
     * 주문을 취소합니다.
     */
    @PatchMapping("/{orderId}/cancel")
    public CommonApiResponse<OrderCancelResponse> cancelOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderCancelRequest request
    ) {
        OrderCancelResponse response = orderService.cancelOrder(orderId, request);

        return CommonApiResponse.success(
                HttpStatus.OK,
                "주문 취소 성공",
                response
        );
    }
}
