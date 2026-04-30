package com.ildang100.backoffice.product.controller;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.AuthUtils;
import com.ildang100.backoffice.common.enums.ProductStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.product.dto.request.ProductCreateRequest;
import com.ildang100.backoffice.product.dto.request.ProductStatusUpdateRequest;
import com.ildang100.backoffice.product.dto.request.ProductStockUpdateRequest;
import com.ildang100.backoffice.product.dto.request.ProductUpdateRequest;
import com.ildang100.backoffice.product.dto.response.PageResponse;
import com.ildang100.backoffice.product.dto.response.ProductDetailResponse;
import com.ildang100.backoffice.product.dto.response.ProductResponse;
import com.ildang100.backoffice.product.policy.ProductSortPolicy;
import com.ildang100.backoffice.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * 상품을 등록합니다.
     *
     * <p>등록 관리자 ID는 JWT 인증 후 SecurityContext에 저장된 값을 사용합니다.</p>
     */
    @PostMapping
    public CommonApiResponse<ProductResponse> create(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        LoginAdminDto loginAdmin = AuthUtils.getLoginAdmin();

        ProductResponse response = productService.create(loginAdmin.getId(), request);

        return CommonApiResponse.success(HttpStatus.CREATED, "상품 생성 완료", response);
    }

    /**
     * 상품 정보를 수정합니다.
     */
    @PutMapping("/{productId}")
    public CommonApiResponse<ProductResponse> update(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductResponse response = productService.update(productId, request);

        return CommonApiResponse.success(HttpStatus.OK, "상품 정보 수정 성공", response);
    }

    /**
     * 상품을 삭제 처리합니다.
     */
    @DeleteMapping("/{productId}")
    public CommonApiResponse<Void> delete(
            @PathVariable Long productId
    ) {
        productService.delete(productId);

        return CommonApiResponse.success(HttpStatus.OK, "상품 삭제 완료", null);
    }

    /**
     * 상품 목록을 조회합니다.
     */
    @GetMapping
    public CommonApiResponse<PageResponse<ProductResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Sort sort = ProductSortPolicy.resolve(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        PageResponse<ProductResponse> response =
                productService.search(keyword, category, status, pageable);

        return CommonApiResponse.success(HttpStatus.OK, "상품 목록 조회 성공", response);
    }

    /**
     * 상품 상세 정보를 조회합니다.
     */
    @GetMapping("/{productId}")
    public CommonApiResponse<ProductDetailResponse> getDetail(
            @PathVariable Long productId
    ) {
        ProductDetailResponse response = productService.getDetail(productId);

        return CommonApiResponse.success(HttpStatus.OK, "상품 상세 조회 성공", response);
    }

    /**
     * 상품 재고를 변경합니다.
     */
    @PutMapping("/{productId}/stock")
    public CommonApiResponse<ProductResponse> changeStock(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStockUpdateRequest request
    ) {
        ProductResponse response = productService.changeStock(productId, request);

        return CommonApiResponse.success(HttpStatus.OK, "상품 재고 변경 성공", response);
    }

    /**
     * 상품 상태를 변경합니다.
     */
    @PutMapping("/{productId}/status")
    public CommonApiResponse<ProductResponse> changeStatus(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStatusUpdateRequest request
    ) {
        ProductResponse response = productService.changeStatus(productId, request);

        return CommonApiResponse.success(HttpStatus.OK, "상품 상태 변경 성공", response);
    }
}
