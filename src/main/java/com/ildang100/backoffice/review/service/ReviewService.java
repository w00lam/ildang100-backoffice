package com.ildang100.backoffice.review.service;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.product.dto.response.PageResponse;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.repository.ProductRepository;
import com.ildang100.backoffice.review.dto.response.ReviewListItemResponse;
import com.ildang100.backoffice.review.entity.Review;
import com.ildang100.backoffice.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 리뷰 도메인 서비스.
 *
 * <p>
 * 본 서비스는 어드민 채널(R-1~R-5)의 리뷰 조회·삭제·통계 책임을 가진다.
 * 본 PR에서는 Story R-1(목록 조회)만 구현되며, R-2/R-3/R-4/R-5는 후속 PR에서 추가된다.
 * </p>
 *
 * @author [작성자]
 * @since 2026-04-29
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    /**
     * 리뷰 목록 조회 (Story R-1).
     *
     * <p>
     * 입력 검증은 Controller 단에서 끝났다고 가정한다 ({@code ProductService#search}와 동일 패턴):
     * <ul>
     *     <li>{@code page} / {@code size} / {@code rating}: Bean Validation
     *         ({@code @Min} / {@code @Max})</li>
     *     <li>{@code sortBy} / {@code sortOrder}: {@code ReviewSortPolicy}</li>
     * </ul>
     * Service는 이미 검증된 {@link Pageable}을 받아 (1) 대상 상품 존재·삭제 검증
     * (2) Repository 위임만 한다.
     * </p>
     *
     * <p>
     * keyword 정규화: {@code null} 또는 blank(공백만 포함)이면 {@code null}로 전달하여
     * JPQL 필터를 미적용으로 처리한다 — API 명세서 §"keyword 빈 문자열 또는 공백만 → 필터 미적용"
     * 요건 충족.
     * </p>
     *
     * @throws ServiceException 상품 없음 또는 DELETED 상태
     *                          ({@link ErrorCode#PRODUCT_NOT_FOUND})
     */
    @Transactional(readOnly = true)
    public PageResponse<ReviewListItemResponse> search(
            Long productId,
            String keyword,
            Integer rating,
            Pageable pageable
                                                      ) {
        Product product = productRepository.findById(productId)
                                           .filter(p -> !p.isDeleted())
                                           .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        Page<Review> reviews = reviewRepository.searchReviews(
                product.getId(),
                normalizedKeyword,
                rating,
                pageable
                                                             );

        return PageResponse.from(reviews.map(ReviewListItemResponse::from));
    }
}

