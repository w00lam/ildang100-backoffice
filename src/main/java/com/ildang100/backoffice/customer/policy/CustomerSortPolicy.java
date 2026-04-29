package com.ildang100.backoffice.customer.policy;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import org.springframework.data.domain.Sort;

/**
 * 고객 목록 조회에서 사용할 정렬 조건을 검증하고 생성하는 정책 클래스입니다.
 *
 * <p>허용된 정렬 필드와 정렬 방향만 {@link Sort}로 변환합니다.</p>
 */
public final class CustomerSortPolicy {

    private CustomerSortPolicy() {
    }

    /**
     * 요청 정렬 조건을 Spring Data 정렬 객체로 변환합니다.
     *
     * @param sortBy 정렬 기준. 허용 값: {@code name}, {@code email}, {@code createdAt}
     * @param sortOrder 정렬 방향. 허용 값: {@code asc}, {@code desc}
     * @return 검증된 정렬 조건
     * @throws ServiceException 정렬 기준 또는 정렬 방향이 유효하지 않은 경우
     */
    public static Sort resolve(String sortBy, String sortOrder) {
        String property = resolveProperty(sortBy);
        Sort.Direction direction = resolveDirection(sortOrder);

        return Sort.by(direction, property);
    }

    private static String resolveProperty(String sortBy) {
        if ("name".equals(sortBy)) {
            return "name";
        }

        if ("email".equals(sortBy)) {
            return "email";
        }

        if ("createdAt".equals(sortBy)) {
            return "createdAt";
        }

        throw new ServiceException(ErrorCode.VALIDATION_FAILED);
    }

    private static Sort.Direction resolveDirection(String sortOrder) {
        if ("asc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.ASC;
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.DESC;
        }

        throw new ServiceException(ErrorCode.VALIDATION_FAILED);
    }
}
