package com.ildang100.backoffice.order.service;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.customer.repository.CustomerRepository;
import com.ildang100.backoffice.order.dto.request.OrderCancelRequest;
import com.ildang100.backoffice.order.dto.request.OrderCreateRequest;
import com.ildang100.backoffice.order.dto.request.OrderStatusUpdateRequest;
import com.ildang100.backoffice.order.dto.response.*;
import com.ildang100.backoffice.order.entity.Order;
import com.ildang100.backoffice.order.repository.OrderRepository;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

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
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.UNAUTHORIZED));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ServiceException(ErrorCode.CUSTOMER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

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
     * 주문 목록 조회 조건을 검증하고 페이지 요청 정보로 변환한 뒤 주문 목록을 조회합니다.
     *
     * <p>{@code page}는 1부터 시작하며, 내부 조회 시 Spring Data의 0 기반 페이지 번호로 변환합니다.
     * 숫자 검색어는 주문 번호 검색에도 사용합니다.</p>
     *
     * @param keyword 고객 이름 또는 주문 번호 검색어. {@code null} 또는 빈 문자열이면 검색 조건 없음
     * @param page 조회할 페이지 번호. 1 이상이어야 함
     * @param size 페이지당 조회할 주문 수. 1 이상이어야 함
     * @param sortBy 정렬 기준. 허용 값: {@code quantity}, {@code totalPrice}, {@code createdAt}
     * @param sortOrder 정렬 방향. 허용 값: {@code asc}, {@code desc}
     * @param status 조회할 주문 상태. {@code null}이면 상태 조건 없음
     * @return 주문 목록 응답 DTO
     * @throws ServiceException 페이지, 크기, 정렬 기준 또는 정렬 방향이 유효하지 않은 경우
     */
    @Transactional(readOnly = true)
    public OrderListResponse getOrders(
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortOrder,
            OrderStatus status
    ) {
        if (page < 1 || size < 1) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }

        String sortProperty = convertOrderSortProperty(sortBy);
        Sort.Direction direction = convertSortDirection(sortOrder);

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(direction, sortProperty)
        );

        Long orderNumber = parseOrderNumber(keyword);
        Page<Order> orders = orderRepository.searchOrders(
                keyword, orderNumber, status, pageable);

        return OrderListResponse.from(orders);
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
        validateOrderId(orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

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
        validateOrderId(orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

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
        validateOrderId(orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

        order.cancel(request.getCancelReason());
        order.getProduct().restoreStock(order.getQuantity());

        return OrderCancelResponse.from(order);
    }

    private Long generateOrderNumber() {
        return Long.parseLong(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
        );
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

    private String convertOrderSortProperty(String sortBy) {
        if ("quantity".equals(sortBy)) {
            return "quantity";
        }

        if ("totalPrice".equals(sortBy)) {
            return "totalPrice";
        }

        if ("createdAt".equals(sortBy)) {
            return "createdAt";
        }

        throw new ServiceException(ErrorCode.VALIDATION_FAILED);
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

    private void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
    }
}
