package com.ildang100.backoffice.product.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;


/**
 * 페이지 공통 응답 DTO입니다.
 *
 * <p>
 * Spring Data {@link Page}를 외부 API 응답용으로 단순화한 페이징 컨테이너입니다.
 * 백오피스 전체(상품/고객/주문 등)의 목록 조회 응답에서 {@code CommonApiResponse<T>}의
 * {@code data} 필드 타입으로 재사용됩니다.
 * </p>
 *
 * <p>
 * Spring Data 내부의 페이지 번호는 0-based이지만, 외부 API에서는 1-based로 노출합니다.
 * {@link #from(Page)}에서 {@code page.getNumber() + 1}로 변환됩니다.
 * Service에서 입력받은 {@code page} 값은 {@code PageRequest.of(page - 1, size, ...)}로
 * 0-based로 변환해서 Repository에 전달해야 합니다.
 * </p>
 *
 * <p>
 * 외부 직접 생성을 막고 {@link #from(Page)} 팩토리 메서드를 통해서만 생성됩니다.
 * </p>
 *
 * @param <T> 페이지 항목 타입
 * @author js-kim-arc
 * @since 2026-04-27
 */
@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    private PageResponse(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
                        ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
