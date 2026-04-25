package com.ildang100.backoffice.common.execption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 에러 코드를 정의하는 Enum입니다.
 *
 * <p>각 에러는 HTTP 상태 코드와 메시지를 함께 관리합니다.</p>
 */
@Getter
public enum ErrorCode {
    // 400 BAD_REQUEST
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 관리자 역할입니다."),
    INVALID_ADMIN_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 관리자 상태입니다."),
    INVALID_CUSTOMER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 고객 상태입니다."),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 상태입니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태입니다."),
    INVALID_APPROVAL_DECISION(HttpStatus.BAD_REQUEST, "유효하지 않은 승인 처리 요청입니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다."),
    INVALID_STOCK_VALUE(HttpStatus.BAD_REQUEST, "재고값이 올바르지 않습니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "주문 수량이 올바르지 않습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ADMIN_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않았거나 비활성 상태의 관리자입니다."),
    INVALID_PASSWORD(HttpStatus.FORBIDDEN, "현재 비밀번호가 올바르지 않습니다."),

    // 404 NOT_FOUND
    INVALID_CREDENTIALS(HttpStatus.NOT_FOUND, "이메일 또는 비밀번호가 올바르지 않습니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "관리자가 존재하지 않습니다."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "고객이 존재하지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),

    // 409 CONFLICT
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    ALREADY_APPROVED_ADMIN(HttpStatus.CONFLICT, "이미 승인 처리된 관리자입니다."),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),
    INVALID_ORDER_STATUS_TRANSITION(HttpStatus.CONFLICT, "허용되지 않는 주문 상태 변경입니다."),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.CONFLICT, "이미 취소되었거나 취소할 수 없는 주문입니다."),
    ADMIN_DELETE_NOT_ALLOWED(HttpStatus.CONFLICT, "삭제할 수 없는 관리자 상태입니다."),
    CUSTOMER_DELETE_NOT_ALLOWED(HttpStatus.CONFLICT, "삭제할 수 없는 고객 상태입니다."),
    PRODUCT_DELETE_NOT_ALLOWED(HttpStatus.CONFLICT, "주문 또는 리뷰가 연결된 상품은 삭제할 수 없습니다."),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
