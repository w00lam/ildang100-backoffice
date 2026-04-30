package com.ildang100.backoffice.order.service;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.service.AdminService;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.customer.service.CustomerService;
import com.ildang100.backoffice.dashboard.dto.RecentOrderResponse;
import com.ildang100.backoffice.order.dto.request.OrderCancelRequest;
import com.ildang100.backoffice.order.dto.request.OrderCreateRequest;
import com.ildang100.backoffice.order.dto.request.OrderStatusUpdateRequest;
import com.ildang100.backoffice.order.dto.response.*;
import com.ildang100.backoffice.order.entity.Order;
import com.ildang100.backoffice.order.repository.OrderRepository;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AdminService adminService;
    private final CustomerService customerService;
    private final ProductService productService;

    private static final int DASHBOARD_RECENT_ORDER_LIMIT = 10;

    /**
     * 주문을 생성합니다.
     *
     * <p>관리자, 고객, 상품을 조회한 뒤 상품 재고를 차감하고 주문 정보를 저장합니다.</p>
     *
     * @param adminId 주문을 생성하는 관리자 ID
     * @param request 주문 생성 요청 정보
     * @return 생성된 주문 응답 DTO
     * @throws ServiceException 관리자를 찾을 수 없거나, 고객/상품을 찾을 수 없거나, 재고 차감이 불가능한 경우
     */
    @Transactional
    public OrderCreateResponse createOrder(Long adminId, OrderCreateRequest request) {
        Admin admin = adminService.getAdminOrThrow(adminId);
        Customer customer = customerService.getCustomerOrThrow(request.getCustomerId());
        Product product = productService.getProductOrThrow(request.getProductId());

        // 주문 생성 전에 단종 또는 삭제된 상품을 차단한다.
        product.assertOrderable();

        product.decreaseStock(request.getQuantity());

        Order order = Order.create(
                admin,
                customer,
                product,
                request.getQuantity(),
                generateOrderNumber()
        );

        return OrderCreateResponse.from(orderRepository.save(order));
    }

    /**
     * 주문번호를 생성합니다.
     *
     * <p>주문번호는 현재 시각(yyyyMMddHHmmssSSS)에 3자리 난수를 붙여 생성합니다.
     * 동일한 밀리초에 여러 주문이 생성될 경우 시간값만으로는 중복될 수 있으므로,
     * 중복 가능성을 낮추기 위해 난수를 함께 사용합니다.</p>
     *
     * <p>최종 중복 방지는 DB의 unique 제약 조건에 의해 보장됩니다.</p>
     *
     * @return 생성된 주문번호
     */
    private Long generateOrderNumber() {
        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        int randomNumber = ThreadLocalRandom.current().nextInt(100, 1000);

        return Long.parseLong(dateTime + randomNumber);
    }

    /**
     * 주문 목록을 페이지 단위로 조회합니다.
     *
     * <p>검색어와 주문 상태 조건을 적용합니다. 숫자 검색어는 주문 번호 검색에도 사용합니다.</p>
     *
     * @param keyword  고객 이름 또는 주문 번호 검색어. {@code null} 또는 빈 문자열이면 검색 조건 없음
     * @param status   조회할 주문 상태. {@code null}이면 상태 조건 없음
     * @param pageable 페이지 및 정렬 정보
     * @return 주문 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public OrderListResponse getOrders(
            String keyword,
            OrderStatus status,
            Pageable pageable
    ) {
        Long orderNumber = parseOrderNumber(keyword);

        Page<Order> orders = orderRepository.searchOrders(
                keyword,
                orderNumber,
                status,
                pageable
        );

        return OrderListResponse.from(orders);
    }

    /**
     * 검색어를 주문 번호 검색 조건으로 변환합니다.
     *
     * <p>검색어가 비어 있거나 숫자로 변환할 수 없으면 주문 번호 조건을 적용하지 않도록 {@code null}을 반환합니다.</p>
     *
     * @param keyword 주문 목록 검색어
     * @return 주문 번호 검색에 사용할 값. 변환할 수 없으면 {@code null}
     */
    private Long parseOrderNumber(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(keyword);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 주문 ID로 주문 상세 정보를 조회합니다.
     *
     * @param orderId 조회할 주문 ID
     * @return 주문 상세 응답 DTO
     * @throws ServiceException 주문 ID가 유효하지 않거나 주문을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);

        return OrderDetailResponse.from(order);
    }

    /**
     * 주문 상태를 수정합니다.
     *
     * @param orderId 상태를 수정할 주문 ID
     * @param request 변경할 주문 상태 정보
     * @return 변경된 주문 상태 응답 DTO
     * @throws ServiceException 주문 ID가 유효하지 않거나, 주문을 찾을 수 없거나, 허용되지 않은 상태 전이인 경우
     */
    @Transactional
    public OrderStatusUpdateResponse updateOrderStatus(
            Long orderId,
            OrderStatusUpdateRequest request
    ) {
        Order order = getOrderOrThrow(orderId);

        order.updateStatus(request.getStatus());

        return OrderStatusUpdateResponse.from(order);
    }

    /**
     * 주문을 취소하고 상품 재고를 복구합니다.
     *
     * @param orderId 취소할 주문 ID
     * @param request 주문 취소 사유 정보
     * @return 취소된 주문 응답 DTO
     * @throws ServiceException 주문 ID가 유효하지 않거나, 주문을 찾을 수 없거나, 취소할 수 없는 주문인 경우
     */
    @Transactional
    public OrderCancelResponse cancelOrder(
            Long orderId,
            OrderCancelRequest request
    ) {
        Order order = getOrderOrThrow(orderId);

        order.cancel(request.getCancelReason());
        order.getProduct().restoreStock(order.getQuantity());

        return OrderCancelResponse.from(order);
    }

    /**
     * 주문 ID로 주문을 조회하고, 없으면 예외를 발생시킵니다.
     *
     * @param orderId 조회할 주문 ID
     * @return 조회된 주문 엔티티
     * @throws ServiceException 주문 ID가 유효하지 않거나 주문을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Order getOrderOrThrow(Long orderId) {
        validateOrderId(orderId);

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }

    /**
     * 대시보드에 표시할 최근 주문 목록을 조회합니다.
     *
     * <p>
     * 현재 처리 대상인 주문(예: PREPARING 상태)을 기준으로 최신 순으로 조회하며,
     * 설정된 최대 개수(DASHBOARD_RECENT_ORDER_LIMIT)만큼 반환합니다.
     * </p>
     *
     * <p>
     * 조회된 주문 엔티티는 외부에 직접 노출하지 않고,
     * {@link RecentOrderResponse} DTO로 변환하여 반환합니다.
     * </p>
     *
     * <p><b>처리 흐름</b></p>
     * <ol>
     *     <li>최근 주문을 최신 생성일 기준으로 조회 (Pageable 적용)</li>
     *     <li>각 주문의 고객명, 상품명 등 필요한 데이터 추출</li>
     *     <li>{@code RecentOrderResponse} DTO로 변환</li>
     * </ol>
     *
     * <p>
     * 대시보드 특성상 실시간성이 중요하므로, 별도의 수정 작업 없이
     * 조회 전용 트랜잭션({@code readOnly = true})으로 수행됩니다.
     * </p>
     *
     * @return 최근 주문 목록 (DTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<RecentOrderResponse> getRecentOrdersForDashboard() {
        return orderRepository.findRecentOrdersBySortByDesc(
                        PageRequest.of(0, DASHBOARD_RECENT_ORDER_LIMIT)
                )
                .stream()
                .map(order -> RecentOrderResponse.of(
                        order.getOrderNumber(),
                        order.getCustomer().getName(),
                        order.getProduct().getName(),
                        order.getTotalPrice(),
                        order.getStatus()
                ))
                .toList();
    }
}
