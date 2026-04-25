package com.ildang100.backoffice.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 공통 API 응답을 위한 Wrapper 클래스입니다.
 *
 * @param <T> 실제 응답 데이터 타입
 */
@Getter
@RequiredArgsConstructor
public class CommonApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    /**
     * 성공 응답 생성
     */
    public static <T> CommonApiResponse<T> success(HttpStatus status, String message, T data) {
        return new CommonApiResponse<>(status.value(), message, data);
    }
}
