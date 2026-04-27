package com.ildang100.backoffice.product.controller;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.product.dto.request.ProductCreateRequest;
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
     * 상품 등록 API
     *
     * <p>
     * 새 상품을 등록합니다. 등록 관리자 ID는 세션 인증 정보에서 식별되며,
     * 요청 본문에는 포함되지 않습니다.
     * </p>
     *
     * @param request 상품 등록 요청 DTO ({@code @Valid}로 형식 검증)
     * @param session HTTP 세션 (로그인된 관리자 식별)
     * @return 등록된 상품 정보를 담은 공통 응답 (HTTP 201)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonApiResponse<ProductResponse> create(
            @RequestBody @Valid ProductCreateRequest request,
            HttpSession session
                                                    ) {
        LoginAdminDto loginAdmin = SessionUtils.getLoginAdmin(session);

        ProductResponse response = productService.create(request, loginAdmin.getId());

        return CommonApiResponse.success(HttpStatus.CREATED, "상품 생성 완료", response);
    }
}
