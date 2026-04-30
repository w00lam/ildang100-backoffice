package com.ildang100.backoffice.product.service;


import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.service.AdminService;
import com.ildang100.backoffice.common.enums.ProductStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.product.dto.request.ProductCreateRequest;
import com.ildang100.backoffice.product.dto.request.ProductStatusUpdateRequest;
import com.ildang100.backoffice.product.dto.request.ProductStockUpdateRequest;
import com.ildang100.backoffice.product.dto.request.ProductUpdateRequest;
import com.ildang100.backoffice.product.dto.response.PageResponse;
import com.ildang100.backoffice.product.dto.response.ProductDetailResponse;
import com.ildang100.backoffice.product.dto.response.ProductResponse;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.repository.ProductRepository;
import com.ildang100.backoffice.review.dto.response.ProductReviewSummary;
import com.ildang100.backoffice.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final AdminService adminService;
    private final ReviewService reviewService;

    private static final int LATEST_REVIEW_LIMIT = 3; //우선 임의로 최대 3개만 하겠습니다.

    /**
     * 상품 등록.
     * 등록 관리자(Admin)를 조회한 뒤 Aggregate.create()에 위임한다.
     */
    public ProductResponse create(Long adminId, ProductCreateRequest request) {
        Admin admin = adminService.getAdminOrThrow(adminId);

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
        Product product = getProductOrThrow(productId);

        product.updateInfo(request.getName(), request.getCategory(), request.getPrice());

        return ProductResponse.from(product);
    }

    /**
     * 상품 삭제 (소프트 삭제 수정, P-6).
     *
     * <p>존재하지 않는 ID에 대해 404 응답을 보장하기 위해 {@code findById} 후
     * Aggregate의 {@code markAsDeleted()}를 호출하여 {@code deletionStatus}만
     * {@code DELETED}로 전이시킨다. 데이터는 물리적으로 삭제되지 않으며,
     * 상품의 {@code status}(판매 상태)는 보존된다.
     *
     * <p>이미 {@code DELETED} 상태인 상품에 대해 재호출 시 Aggregate에서
     * {@code PRODUCT_ALREADY_DELETED}(409)를 던진다.
     *
     * <p>본 epic 범위에서는 참조 무결성 검증을 별도로 수행하지 않는다 — soft delete의
     * 본질상 데이터가 보존되므로 주문/리뷰 연결 여부와 무관하게 처리 가능하다.
     */
    public void delete(Long productId) {
        Product product = getProductOrThrow(productId);

        product.markAsDeleted();
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

    @Transactional(readOnly = true)
    public ProductDetailResponse getDetail(Long productId) {
        Product product = productRepository.findDetailById(productId)
                                           .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        // ⚠️ DELETED 검증 추가 필요 — 현재 코드는 DELETED 상품도 응답함
        if (product.isDeleted()) {
            throw new ServiceException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        ProductReviewSummary reviewSummary = reviewService.getSummary(productId, LATEST_REVIEW_LIMIT);

        return ProductDetailResponse.from(product, reviewSummary);
    }

    /**
     * 상품 재고 변경 (운영자 채널).
     *
     * <p>
     * 재고를 절대값으로 설정하며, 도메인 정책에 의해 상태가 자동 전이된다.
     * 단종 상품은 재고만 변경되고 상태는 유지된다.
     * </p>
     */
    public ProductResponse changeStock(Long productId, ProductStockUpdateRequest request) {
        Product product = getProductOrThrow(productId);

        product.changeStock(request.getStock());

        return ProductResponse.from(product);
    }

    /**
     * 상품 상태 변경 (운영자 채널 / P-5).
     *
     * <p>
     * 운영자의 명시적 의사결정으로 상태를 변경한다. P-4의 자동 전이 정책과 독립적으로
     * 동작하며, 본 메서드로 단종 설정 후 재고를 변경해도 상태는 단종으로 유지된다.
     * </p>
     */
    public ProductResponse changeStatus(Long productId, ProductStatusUpdateRequest request) {
        Product product = getProductOrThrow(productId);

        product.changeStatus(request.getStatus());

        return ProductResponse.from(product);
    }

    /**
     * 상품 ID로 상품을 조회하고, 없으면 예외를 발생시킵니다.
     *
     * @param productId 조회할 상품 ID
     * @return 조회된 상품 엔티티
     * @throws ServiceException 상품을 찾을 수 없는 경우
     */
    public Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
