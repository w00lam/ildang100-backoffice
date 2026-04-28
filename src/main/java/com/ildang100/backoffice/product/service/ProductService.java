package com.ildang100.backoffice.product.service;


import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.common.enums.ProductStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.product.dto.request.ProductCreateRequest;
import com.ildang100.backoffice.product.dto.request.ProductUpdateRequest;
import com.ildang100.backoffice.product.dto.response.PageResponse;
import com.ildang100.backoffice.product.dto.response.ProductResponse;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;

    /**
     * 상품 등록.
     * 등록 관리자(Admin)를 조회한 뒤 Aggregate.create()에 위임한다.
     */
    public ProductResponse create(Long adminId, ProductCreateRequest request) {
        Admin admin = adminRepository.findById(adminId)
                                     .orElseThrow(() -> new ServiceException(ErrorCode.UNAUTHORIZED));

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

    /**
     * 상품 정보 부분 수정.
     */
    public ProductResponse update(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateInfo(request.getName(), request.getCategory(), request.getPrice());

        return ProductResponse.from(product);
    }

    /**
     * 상품 삭제 (물리 삭제, P-1 ~ P-2 단계).
     *
     * <p>존재하지 않는 ID에 대해 404 응답을 보장하기 위해 {@code findById} 후
     * {@code delete(entity)}로 처리한다. {@code deleteById(id)}는 존재하지 않는
     * ID를 전달해도 조용히 종료되는 경우가 있어 {@code PRODUCT_NOT_FOUND} 예외가
     * 일관되게 던져지지 않는다.
     *
     * <p>참조 무결성 검증(주문/리뷰 연결)은 Story P-6에서 추가 예정.
     */
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }

    /**
     * 상품 목록 조회 (페이징·검색·필터·정렬).
     *
     * <p>
     * 입력 검증은 Controller 단에서 끝났다고 가정한다:
     * - {@code page} / {@code size}: Bean Validation ({@code @Min}/{@code @Max})
     * - {@code sortBy} / {@code sortOrder}: {@code ProductSortPolicy}
     * Service는 이미 검증된 {@link Pageable}을 받아 Repository에 위임만 한다.
     * </p>
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(
            String keyword,
            String category,
            ProductStatus status,
            Pageable pageable
                                               ) {
        Page<Product> products = productRepository.searchProducts(keyword, category, status, pageable);
        return PageResponse.from(products.map(ProductResponse::from));
    }

}