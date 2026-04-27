package com.ildang100.backoffice.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    /** 상품명. null이면 변경하지 않음. 비어있으면 Aggregate에서 거부. */
    @Size(max = 30, message = "상품명은 최대 30자까지 가능합니다.")
    private String name;

    /** 카테고리. null이면 변경하지 않음. 비어있으면 Aggregate에서 거부. */
    @Size(max = 30, message = "카테고리는 최대 30자까지 가능합니다.")
    private String category;

    /** 가격. null이면 변경하지 않음. */
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
}