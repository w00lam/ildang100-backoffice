package com.ildang100.backoffice.review.service;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.product.dto.response.PageResponse;
import com.ildang100.backoffice.product.entity.Product;
import com.ildang100.backoffice.product.repository.ProductRepository;
import com.ildang100.backoffice.review.dto.response.ReviewDetailResponse;
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

    /**
     * 리뷰 상세 조회 (Story R-2).
     *
     * <p>
     * 소프트 삭제된 리뷰는 운영자에게도 노출되지 않으며 {@code REVIEW_NOT_FOUND}(404)로
     * 응답합니다. URL 경로상 productId와 실제 리뷰의 product가 다른 경우도
     * 동일하게 {@code REVIEW_NOT_FOUND}로 처리합니다 — 잘못된 URL 컨텍스트로
     * 정상 응답이 나가지 않도록 차단.
     * </p>
     *
     * @throws ServiceException 상품 없음 또는 DELETED ({@link ErrorCode#PRODUCT_NOT_FOUND}),
     *                          리뷰 없음·DELETED·다른 상품 소속 ({@link ErrorCode#REVIEW_NOT_FOUND})
     */
    @Transactional(readOnly = true)
    public ReviewDetailResponse getDetail(Long productId, Long reviewId) {
        productRepository.findById(productId)
                         .filter(p -> !p.isDeleted())
                         .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        Review review = reviewRepository.findDetailByProductIdAndId(productId, reviewId)
                                        .orElseThrow(() -> new ServiceException(ErrorCode.REVIEW_NOT_FOUND));

        return ReviewDetailResponse.from(review);
    }

    /**
     * 리뷰 소프트 삭제 (Story R-3).
     *
     * <p>
     * 흐름:
     * <ol>
     *     <li>대상 상품 존재·삭제 검증 (R-1·R-2와 동일 패턴)</li>
     *     <li>리뷰 조회 — productId 정합성도 쿼리 레벨에서 검증</li>
     *     <li>{@link Review#markAsDeleted()} 호출 — JPA dirty checking이
     *         트랜잭션 종료 시 자동 flush (테이블 모델링 §6.5)</li>
     * </ol>
     * </p>
     *
     * <p>
     * 이미 {@code DELETED} 상태인 리뷰는 {@code Review#markAsDeleted()}가 던지는
     * {@link ErrorCode#REVIEW_ALREADY_DELETED}(409)로 응답합니다 — 본 Service는
     * 별도 가드를 두지 않고 도메인 메서드의 검증을 신뢰합니다.
     * </p>
     *
     * @throws ServiceException 상품 없음 또는 DELETED ({@link ErrorCode#PRODUCT_NOT_FOUND}),
     *                          리뷰 없음·다른 상품 소속 ({@link ErrorCode#REVIEW_NOT_FOUND}),
     *                          이미 DELETED 상태 ({@link ErrorCode#REVIEW_ALREADY_DELETED})
     */
    @Transactional
    public void delete(Long productId, Long reviewId) {
        productRepository.findById(productId)
                         .filter(p -> !p.isDeleted())
                         .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

        Review review = reviewRepository.findByProductIdAndId(productId, reviewId)
                                        .orElseThrow(() -> new ServiceException(ErrorCode.REVIEW_NOT_FOUND));

        review.markAsDeleted();
    }
}

