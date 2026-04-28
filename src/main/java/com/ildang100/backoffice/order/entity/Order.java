package com.ildang100.backoffice.order.entity;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.OrderStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.customer.entity.Customer;
import com.ildang100.backoffice.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 정보를 저장하는 엔티티입니다.
 *
 * <p>주문 고객, 상품, 담당 관리자, 주문 수량, 주문 금액과 주문 상태를 관리합니다.</p>
 */
@Getter
@Entity
@Table (name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "order_number", nullable = false, unique = true)
    private Long orderNumber;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    /**
     * 주문 엔티티를 생성합니다.
     *
     * <p>주문 생성 시 상태는 {@code PREPARING}으로 설정하고, 상품의 현재 가격과 주문 수량으로 총 금액을 계산합니다.</p>
     *
     * @param admin 주문을 생성한 관리자
     * @param customer 주문 고객
     * @param product 주문 상품
     * @param quantity 주문 수량
     * @param orderNumber 주문 번호
     * @return 생성된 주문 엔티티
     * @throws ServiceException 필수 값이 없거나 주문 수량이 유효하지 않은 경우
     */
    public static Order create(
            Admin admin,
            Customer customer,
            Product product,
            Integer quantity,
            Long orderNumber
    ) {
        if (admin == null || customer == null || product == null || orderNumber == null) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }

        if (quantity == null || quantity < 1) {
            throw new ServiceException(ErrorCode.INVALID_QUANTITY);
        }

        Order order = new Order();
        order.admin = admin;
        order.customer = customer;
        order.product = product;
        order.quantity = quantity;
        order.status = OrderStatus.PREPARING;
        order.orderNumber = orderNumber;
        order.unitPrice = product.getPrice();
        order.totalPrice = product.getPrice() * quantity;

        return order;
    }

    public void updateStatus(OrderStatus nextStatus) {
        if (!canChangeStatus(nextStatus)) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        this.status = nextStatus;
    }

    private boolean canChangeStatus(OrderStatus nextStatus) {
        if (this.status == OrderStatus.PREPARING && nextStatus == OrderStatus.SHIPPING) {
            return true;
        }

        if (this.status == OrderStatus.SHIPPING && nextStatus == OrderStatus.DELIVERED) {
            return true;
        }

        return false;
    }
}
