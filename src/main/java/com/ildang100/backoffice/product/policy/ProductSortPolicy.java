package com.ildang100.backoffice.product.policy;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class ProductSortPolicy {

    private static final Set<String> ALLOWED_PROPERTIES = Set.of("createdAt", "price", "stock");

    private ProductSortPolicy() {
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
