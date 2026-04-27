package com.ildang100.backoffice.product.service;


import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.product.dto.request.ProductCreateRequest;
import com.ildang100.backoffice.product.dto.response.ProductResponse;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;

    /**
     * 상품 등록 처리
     *
     * <p>
     * 요청받은 정보를 기반으로 새로운 상품을 생성합니다.
     * 등록 관리자는 세션 인증 정보에서 식별된 ID로 조회하며,
     * Product Aggregate의 팩토리 메서드를 통해 도메인 검증과 상태 자동 결정을 적용합니다.
     * </p>
     *
     * @param request 상품 등록 요청 DTO
     * @param adminId 등록 관리자 ID (Controller에서 세션을 통해 식별)
     * @return 등록된 상품 정보 응답 DTO
     * @throws ServiceException 등록 관리자가 존재하지 않거나 도메인 검증 실패 시
     */
    @Transactional
    public ProductResponse create(ProductCreateRequest request, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                                     .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        Product product = Product.create(
                admin,
                request.getName(),
                request.getCategory(),
                request.getPrice(),
                request.getStock(),
                request.getStatus()
                                        );

        Product saved = productRepository.save(product);

        return ProductResponse.from(saved);
    }
}