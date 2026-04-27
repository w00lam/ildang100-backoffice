package com.ildang100.backoffice.product.dto.request;

import com.ildang100.backoffice.common.enums.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 상품 등록 요청 DTO입니다.
 *
 * <p>
 * 새 상품을 등록할 때 클라이언트가 전달하는 입력 값을 담습니다.
 * 모든 필드는 {@code @Valid} 검증을 통해 형식 검증이 수행되며,
 * 검증 실패 시 {@code GlobalExceptionHandler}에서 {@code VALIDATION_FAILED} 응답으로 처리됩니다.
 * </p>
 *
 * <p>
 * 등록 관리자(adminId)는 본 DTO에 포함되지 않으며,
 * Controller에서 세션 인증 정보(SessionUtils.getLoginAdmin)로 식별됩니다.
 * </p>
 *
 * <p><b>요청 필드</b></p>
 * <ul>
 *     <li>name: 상품명</li>
 *     <li>category: 카테고리</li>
 *     <li>price: 판매 가격</li>
 *     <li>stock: 재고 수량</li>
 *     <li>status: 요청 판매 상태 (도메인 정책에 따라 자동 보정될 수 있음)</li>
 * </ul>
 *
 * @author js-kim-arc
 * @since 2026-04-27
 */
@Getter
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 30, message = "상품명은 최대 30자까지 입력할 수 있습니다.")
    private String name;

    @NotBlank(message = "카테고리는 필수입니다.")
    @Size(max = 30, message = "카테고리는 최대 30자까지 입력할 수 있습니다.")
    private String category;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "재고 수량은 필수입니다.")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stock;

    @NotNull(message = "상품 상태는 필수입니다.")
    private ProductStatus status;
}