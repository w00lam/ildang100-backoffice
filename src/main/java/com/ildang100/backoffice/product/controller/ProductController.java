package com.ildang100.backoffice.product.controller;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.product.dto.request.ProductCreateRequest;
import com.ildang100.backoffice.product.dto.request.ProductUpdateRequest;
import com.ildang100.backoffice.product.dto.response.ProductResponse;
import com.ildang100.backoffice.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록.
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
     * 상품 삭제 (물리 삭제).
     * 참조 무결성 검증은 Story P-6에서 추가 예정.
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
}
