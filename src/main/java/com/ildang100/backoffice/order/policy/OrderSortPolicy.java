package com.ildang100.backoffice.order.policy;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * 주문 목록 조회에서 사용할 정렬 조건을 검증하고 생성하는 정책 클래스입니다.
 *
 * <p>허용된 정렬 필드와 정렬 방향만 {@link Sort}로 변환합니다.</p>
 */
public final class OrderSortPolicy {

    private static final Set<String> ALLOWED_PROPERTIES =
            Set.of("quantity", "totalPrice", "createdAt");

    private OrderSortPolicy() {
    }

    /**
     * 요청 정렬 조건을 Spring Data 정렬 객체로 변환합니다.
     *
     * @param sortBy 정렬 기준. 허용 값: {@code quantity}, {@code totalPrice}, {@code createdAt}
     * @param sortOrder 정렬 방향. 허용 값: {@code asc}, {@code desc}
     * @return 검증된 정렬 조건
     * @throws ServiceException 정렬 기준 또는 정렬 방향이 유효하지 않은 경우
     */
    public static Sort resolve(String sortBy, String sortOrder) {
        return Sort.by(parseDirection(sortOrder), parseProperty(sortBy));
    }

    private static String parseProperty(String sortBy) {
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
