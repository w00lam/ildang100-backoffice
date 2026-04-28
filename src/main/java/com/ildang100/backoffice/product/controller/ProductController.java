package com.ildang100.backoffice.product.controller;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.SessionUtils;
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
import jakarta.servlet.http.HttpSession;
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
     * 상품 등록.
     * 등록 관리자 ID는 세션에서 식별. 요청 본문의 adminId는 받지 않는다.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonApiResponse<ProductResponse> create(
            @Valid @RequestBody ProductCreateRequest request,
            HttpSession session
                                                    ) {
        LoginAdminDto loginAdmin = SessionUtils.getLoginAdmin(session);

        ProductResponse response = productService.create(loginAdmin.getId(), request);

        return CommonApiResponse.success(HttpStatus.CREATED, "상품 생성 완료", response);
    }

    /**
     * 상품 정보 부분 수정.
     * 모든 필드가 null이면 변경 없이 정상 응답 (멱등성 보장).
     */
    @PutMapping("/{productId}")
    public CommonApiResponse<ProductResponse> update(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request,
            HttpSession session
                                                    ) {
        SessionUtils.getLoginAdmin(session); // 인증 가드 (미인증 시 401 자동 발생)

        ProductResponse response = productService.update(productId, request);

        return CommonApiResponse.success(HttpStatus.OK, "상품 정보 수정 성공", response);
    }

    /**
     * 상품 삭제 (소프트 삭제 / P-6).
     * Aggregate의 {@code deletionStatus}를 {@code DELETED}로 전이시키며, 데이터는
     * 물리적으로 삭제되지 않는다. 상품의 판매 상태({@code status})는 보존된다.
     * 이미 {@code DELETED} 상태인 상품을 재삭제 요청하면 409 {@code PRODUCT_ALREADY_DELETED}.
     */
    @DeleteMapping("/{productId}")
    public CommonApiResponse<Void> delete(
            @PathVariable Long productId,
            HttpSession session
                                         ) {
        SessionUtils.getLoginAdmin(session); // 인증 가드

        productService.delete(productId);

        return CommonApiResponse.success(HttpStatus.OK, "상품 삭제 완료", null);
    }

    /**
     * 상품 목록 조회 (페이징·검색·필터·정렬).
     *
     * <p>
     * page는 외부 API 기준 1-based. status가 ProductStatus enum에 매핑되지 않으면
     * Spring의 {@code MethodArgumentTypeMismatchException}으로 떨어지며,
     * GlobalExceptionHandler에서 {@code INVALID_PRODUCT_STATUS}(400)로 매핑된다.
     * </p>
     */
    @GetMapping
    public CommonApiResponse<PageResponse<ProductResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,            // ⬅ 추가
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            HttpSession session
                                                                  ) {
        SessionUtils.getLoginAdmin(session);

        Sort sort = ProductSortPolicy.resolve(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        PageResponse<ProductResponse> response =
                productService.search(keyword, category, status, pageable);   // ⬅ category 전달

        return CommonApiResponse.success(HttpStatus.OK, "상품 목록 조회 성공", response);
    }

    /**
     * 상품 상세 조회.
     * 등록 관리자 이름과 이메일을 함께 반환한다.
     */
    @GetMapping("/{productId}")
    public CommonApiResponse<ProductDetailResponse> getDetail(
            @PathVariable Long productId,
            HttpSession session
                                                             ) {
        SessionUtils.getLoginAdmin(session); // 인증 가드 (미인증 시 401 자동 발생)

        ProductDetailResponse response = productService.getDetail(productId);

        return CommonApiResponse.success(HttpStatus.OK, "상품 상세 조회 성공", response);
    }

    /**
     * 상품 재고 변경 (운영자 채널).
     * 재고 절대값을 설정하며, 도메인 정책에 의해 상태가 자동 전이된다.
     */
    @PutMapping("/{productId}/stock")
    public CommonApiResponse<ProductResponse> changeStock(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStockUpdateRequest request,
            HttpSession session
                                                         ) {
        SessionUtils.getLoginAdmin(session); // 인증 가드

        ProductResponse response = productService.changeStock(productId, request);

        return CommonApiResponse.success(HttpStatus.OK, "상품 재고 변경 성공", response);
    }

    /**
     * 상품 상태 변경 (운영자 채널).
     * 운영자의 명시적 의사결정으로 상태를 변경한다. 자동 전이 정책과 독립적으로 동작.
     */
    @PutMapping("/{productId}/status")
    public CommonApiResponse<ProductResponse> changeStatus(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStatusUpdateRequest request,
            HttpSession session
                                                          ) {
        SessionUtils.getLoginAdmin(session); // 인증 가드

        ProductResponse response = productService.changeStatus(productId, request);

        return CommonApiResponse.success(HttpStatus.OK, "상품 상태 변경 성공", response);
    }
}
