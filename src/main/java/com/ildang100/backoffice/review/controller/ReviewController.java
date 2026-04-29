package com.ildang100.backoffice.review.controller;

import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import com.ildang100.backoffice.product.dto.response.PageResponse;
import com.ildang100.backoffice.review.dto.response.ReviewDetailResponse;
import com.ildang100.backoffice.review.dto.response.ReviewListItemResponse;
import com.ildang100.backoffice.review.policy.ReviewSortPolicy;
import com.ildang100.backoffice.review.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 리뷰 도메인 컨트롤러 (어드민 채널).
 *
 * <p>
 * 본 PR에서는 Story R-1(목록 조회)만 구현된다. R-2(상세) / R-3(삭제)는 후속 PR에서
 * 동일 컨트롤러에 추가된다 — URL 구조상 모두 {@code /admin/products/{productId}/reviews}
 * prefix를 공유.
 * </p>
 *
 * <p>
 * {@code @Validated}는 {@code @RequestParam} 단의 {@code @Min} / {@code @Max} 검증을
 * 활성화하기 위해 클래스 단위로 적용된다 ({@code ProductController}와 동일 패턴).
 * </p>
 *
 * @author [작성자]
 * @since 2026-04-29
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products/{productId}/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 목록 조회 (Story R-1).
     *
     * <p>
     * 입력 검증을 컨트롤러에서 모두 마친 뒤 Service에는 이미 검증된 {@link Pageable}만 전달한다
     * ({@code ProductController#search}와 동일 패턴):
     * <ul>
     *     <li>{@code page} / {@code size} / {@code rating}: Bean Validation
     *         ({@code @Min} / {@code @Max})</li>
     *     <li>{@code sortBy} / {@code sortOrder}: {@link ReviewSortPolicy#resolve}</li>
     * </ul>
     * </p>
     *
     * <p>
     * 페이지 번호 표면은 외부 API 기준 1-based이며, {@code PageRequest.of(page - 1, ...)}로
     * 0-based 변환되어 Repository에 전달된다.
     * 기본 정렬은 {@code createdAt DESC} (최신 작성순).
     * </p>
     */
    @GetMapping
    public CommonApiResponse<PageResponse<ReviewListItemResponse>> search(
            @PathVariable Long productId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @Min(1) @Max(5) Integer rating,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            HttpSession session
                                                                         ) {
        SessionUtils.getLoginAdmin(session); // 인증 가드 (미인증 시 401 자동 발생)

        Sort sort = ReviewSortPolicy.resolve(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        PageResponse<ReviewListItemResponse> response =
                reviewService.search(productId, keyword, rating, pageable);

        return CommonApiResponse.success(HttpStatus.OK, "리뷰 리스트 조회 성공", response);
    }

    /**
     * 리뷰 상세 조회 (Story R-2).
     *
     * <p>
     * 어드민 채널 — 작성 고객의 이메일(customerEmail)을 포함한 상세 정보를 반환합니다.
     * 소프트 삭제된 리뷰는 운영자에게도 노출되지 않으며 404 {@code REVIEW_NOT_FOUND}로
     * 응답합니다.
     * </p>
     */
    @GetMapping("/{reviewId}")
    public CommonApiResponse<ReviewDetailResponse> getDetail(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            HttpSession session
                                                            ) {
        SessionUtils.getLoginAdmin(session); // 인증 가드 (미인증 시 401 자동 발생)

        ReviewDetailResponse response = reviewService.getDetail(productId, reviewId);

        return CommonApiResponse.success(HttpStatus.OK, "리뷰 상세 조회 성공", response);
    }
}
