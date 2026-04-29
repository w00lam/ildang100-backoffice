package com.ildang100.backoffice.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 주문 생성 요청 DTO입니다.
 */
@Getter
public class OrderCreateRequest {

    @NotNull(message = "고객 ID는 필수입니다.")
    @Min(value = 1, message = "고객 ID는 양의 정수여야 합니다.")
    private Long customerId;

    @NotNull(message = "상품 ID는 필수입니다.")
    @Min(value = 1, message = "상품 ID는 양의 정수여야 합니다.")
    private Long productId;

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    private Integer quantity;
}
