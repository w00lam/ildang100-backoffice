package com.ildang100.backoffice.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 재고 변경 요청 DTO입니다 (Story P-4).
 *
 * <p>
 * 운영자 채널 엔드포인트({@code PUT /admin/products/{productId}/stock})의 요청 본문입니다.
 * 재고 절대값을 받아 도메인 메서드 {@code Product#changeStock}에 위임합니다.
 * </p>
 *
 * <p>
 * 음수 재고 검증은 의도적으로 DTO 레이어에 두지 않고 Aggregate({@code validateStock})에서
 * {@code INVALID_STOCK_VALUE}로 발생시킵니다 — API 명세서의 응답 코드 매핑과 맞추기 위함입니다.
 * </p>
 *
 * @author js-kim-arc
 * @since 2026-04-28
 */
@Getter
@NoArgsConstructor
public class ProductStockUpdateRequest {

    @NotNull(message = "재고 수량은 필수입니다.")
    private Integer stock;
}