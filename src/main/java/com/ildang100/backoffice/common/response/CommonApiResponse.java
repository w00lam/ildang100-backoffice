package com.ildang100.backoffice.common.response;

import com.ildang100.backoffice.common.exception.ErrorCode;
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
    private final String code;
    private final String message;
    private final T data;

    /**
     * 성공 응답 생성
     */
    public static <T> CommonApiResponse<T> success(HttpStatus status, String message, T data) {
        return new CommonApiResponse<>(status.value(), message, null, data);
    }

    /**
     * 에러 응답 생성
     */
    public static CommonApiResponse<Void> error(ErrorCode errorCode) {
        return new CommonApiResponse<>(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), null);
    }
}
