package com.ildang100.backoffice.review.policy;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * 리뷰 목록 조회의 sortBy / sortOrder 화이트리스트 정책 (Story R-1).
 *
 * <p>
 * 도메인 정책을 캡슐화한 정적 유틸 클래스. {@code ProductSortPolicy}와 동일한 패턴 —
 * 백오피스 전체에서 정렬 입력 처리를 일관되게 가져간다.
 * </p>
 *
 * <p>
 * sortBy 비교는 case-sensitive (createdAt vs CREATEDAT 불가).
 * sortOrder 비교는 case-insensitive (asc / ASC / Asc 모두 허용).
 * </p>
 *
 * @author [작성자]
 * @since 2026-04-29
 */
public final class ReviewSortPolicy {

    private static final Set<String> ALLOWED_PROPERTIES = Set.of("createdAt", "rating");

    private ReviewSortPolicy() {
        // 인스턴스화 방지
    }

    public static Sort resolve(String sortBy, String sortOrder) {
        return Sort.by(parseDirection(sortOrder), parseProperty(sortBy));
    }

    private static String parseProperty(String sortBy) {
        // Set.of(...).contains(null)은 NPE를 던지므로 명시적 null 가드
        if (sortBy == null || !ALLOWED_PROPERTIES.contains(sortBy)) {
            throw new ServiceException(ErrorCode.VALIDATION_FAILED);
        }
        return sortBy;
    }

    private static Sort.Direction parseDirection(String sortOrder) {
        if ("asc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.DESC;
        }
        throw new ServiceException(ErrorCode.VALIDATION_FAILED);
    }
}
