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
 * 등록 관리자 ID는 요청 본문으로 받지 않고,
 * JWT 인증 후 SecurityContext에 저장된 로그인 관리자 정보에서 가져옵니다.
 * </p>
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
