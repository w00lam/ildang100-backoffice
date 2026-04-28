package com.ildang100.backoffice.product.dto.request;

import com.ildang100.backoffice.common.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 상태 변경 요청 DTO입니다 (Story P-5).
 *
 * <p>
 * 운영자 채널 엔드포인트({@code PUT /admin/products/{productId}/status})의 요청 본문입니다.
 * 운영자의 명시적 의사결정으로 상품 상태를 변경할 때 사용되며, P-4의 자동 전이 정책과
 * 독립적으로 동작합니다.
 * </p>
 *
 * <p>
 * enum 매핑 실패는 Jackson 역직렬화 단계에서 {@code HttpMessageNotReadableException}으로
 * 떨어지며, {@code GlobalExceptionHandler}에서 {@code INVALID_PRODUCT_STATUS}(400)로
 * 매핑됩니다. 본 DTO는 누락 검증({@code @NotNull})만 책임집니다.
 * </p>
 *
 * @author js-kim-arc
 * @since 2026-04-28
 */
@Getter
@NoArgsConstructor
public class ProductStatusUpdateRequest {

    @NotNull(message = "상품 상태는 필수입니다.")
    private ProductStatus status;
}
