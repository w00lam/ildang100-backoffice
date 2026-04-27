package com.ildang100.backoffice.common.exception;

import com.ildang100.backoffice.common.enums.*;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * 전역 예외 처리(Global Exception Handler) 클래스입니다.
 *
 * <p>
 * 애플리케이션 전반에서 발생하는 예외를 중앙에서 처리하여,
 * 클라이언트에게 일관된 응답 형식을 제공하기 위해 사용됩니다.
 * </p>
 *
 * <p><b>처리 대상 예외</b></p>
 * <ul>
 *     <li>{@link ServiceException} : 비즈니스 로직에서 발생한 사용자 정의 예외</li>
 *     <li>{@link MethodArgumentNotValidException} : {@code @Valid} 검증 실패 예외</li>
 *     <li>{@link Exception} : 그 외 모든 예외 (fallback)</li>
 * </ul>
 *
 * <p><b>응답 형식</b></p>
 * <pre>
 * {
 *   "status": 400,
 *   "code": "ERROR_CODE",
 *   "message": "에러 메시지",
 *   "data": null
 * }
 * </pre>
 *
 * @author 이우람
 * @since 2026-04-25
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     *
     * <p>
     * 서비스 계층에서 발생한 {@link ServiceException}을 처리합니다.
     * ErrorCode에 정의된 HTTP 상태 코드와 메시지를 기반으로 응답을 생성합니다.
     * </p>
     *
     * @param e ServiceException
     * @return 공통 에러 응답
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<CommonApiResponse<Void>> serviceException(ServiceException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonApiResponse.error(errorCode));
    }

    /**
     * Valid 검증 실패 예외 처리
     *
     * <p>
     * 요청 DTO에 대한 검증(@Valid) 실패 시 발생하는 예외를 처리합니다.
     * 현재는 공통 ErrorCode(VALUE_VALIDATION_FAILED)를 반환하며,
     * 필요 시 필드별 에러 메시지 확장 가능
     * </p>
     *
     * @param e MethodArgumentNotValidException
     * @return 공통 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonApiResponse<List<String>>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        List<String> messages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(CommonApiResponse.error(ErrorCode.VALIDATION_FAILED, messages));
    }

    /**
     * 예상하지 못한 모든 예외 처리 (Fallback)
     *
     * <p>
     * 처리되지 않은 모든 예외를 캐치하여,
     * 서버 내부 오류(INTERNAL_SERVER_ERROR)로 응답합니다.
     * </p>
     *
     * @param e Exception
     * @return 공통 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(CommonApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        ErrorCode errorCode = resolveTypeMismatchErrorCode(e.getRequiredType());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonApiResponse.error(errorCode));
    }

    private ErrorCode resolveTypeMismatchErrorCode(Class<?> requiredType) {
        if (requiredType == null) {
            return ErrorCode.VALIDATION_FAILED;
        }

        if (requiredType == CustomerStatus.class) {
            return ErrorCode.INVALID_CUSTOMER_STATUS;
        }

        if (requiredType == AdminStatus.class) {
            return ErrorCode.INVALID_ADMIN_STATUS;
        }

        if (requiredType == ProductStatus.class) {
            return ErrorCode.INVALID_PRODUCT_STATUS;
        }

        if (requiredType == OrderStatus.class) {
            return ErrorCode.INVALID_ORDER_STATUS;
        }

        if (requiredType == AdminRole.class) {
            return ErrorCode.INVALID_ROLE;
        }

        return ErrorCode.VALIDATION_FAILED;
    }
}
