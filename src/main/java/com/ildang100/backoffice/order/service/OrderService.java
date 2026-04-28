package com.ildang100.backoffice.order.service;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.customer.repository.CustomerRepository;
import com.ildang100.backoffice.order.dto.OrderCreateRequest;
import com.ildang100.backoffice.order.dto.OrderCreateResponse;
import com.ildang100.backoffice.order.entity.Order;
import com.ildang100.backoffice.order.repository.OrderRepository;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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
     * @param adminid 주문을 생성하는 관리자 ID
     * @param request 주문 생성 요청 정보
     * @return 생성된 주문 응답 DTO
     * @throws ServiceException 관리자를 찾을 수 없거나, 고객/상품을 찾을 수 없거나, 재고 차감이 불가능한 경우
     */
    @Transactional
    public OrderCreateResponse createOrder(Long adminid, OrderCreateRequest request) {
        Admin admin = adminRepository.findById(adminid)
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

    private Long generateOrderNumber() {
        return Long.parseLong(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
        );
    }

}
