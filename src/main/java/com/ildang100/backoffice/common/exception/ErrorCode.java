package com.ildang100.backoffice.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 에러 코드를 정의하는 Enum입니다.
 *
 * <p>각 에러는 HTTP 상태 코드와 메시지를 함께 관리합니다.</p>
 */
@Getter
public enum ErrorCode {
    VALIDATION_FAILED(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_FAILED",
            "입력값이 올바르지 않습니다."
    ),
    INVALID_ROLE(
            HttpStatus.BAD_REQUEST,
            "INVALID_ROLE",
            "유효하지 않은 관리자 역할입니다."
    ),
    INVALID_ADMIN_STATUS(
            HttpStatus.BAD_REQUEST,
            "INVALID_ADMIN_STATUS",
            "유효하지 않은 관리자 상태입니다."
    ),
    INVALID_CUSTOMER_STATUS(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_STATUS",
            "유효하지 않은 고객 상태입니다."
    ),
    INVALID_PRODUCT_STATUS(
            HttpStatus.BAD_REQUEST,
            "INVALID_PRODUCT_STATUS",
            "유효하지 않은 상품 상태입니다."
    ),
    INVALID_ORDER_STATUS(
            HttpStatus.BAD_REQUEST,
            "INVALID_ORDER_STATUS",
            "유효하지 않은 주문 상태입니다."
    ),
    INVALID_APPROVAL_DECISION(
            HttpStatus.BAD_REQUEST,
            "INVALID_APPROVAL_DECISION",
            "유효하지 않은 승인 처리 요청입니다."
    ),
    PASSWORD_CONFIRM_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "PASSWORD_CONFIRM_MISMATCH",
            "비밀번호 확인이 일치하지 않습니다."
    ),
    INVALID_STOCK_VALUE(
            HttpStatus.BAD_REQUEST,
            "INVALID_STOCK_VALUE",
            "재고값이 올바르지 않습니다."
    ),
    INVALID_QUANTITY(
            HttpStatus.BAD_REQUEST,
            "INVALID_QUANTITY",
            "주문 수량이 올바르지 않습니다."
    ),
    /**
     * 가입 거절 처리 시 거절 사유가 누락된 경우 (400 Bad Request)
     */
    REJECT_REASON_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "REJECT_REASON_REQUIRED",
            "거절 사유는 필수입니다."
    ),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "UNAUTHORIZED",
            "로그인이 필요합니다."
    ),

    // 403 FORBIDDEN
    FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "FORBIDDEN",
            "접근 권한이 없습니다."
    ),
    ADMIN_PENDING_APPROVAL(
            HttpStatus.FORBIDDEN,
            "ADMIN_PENDING_APPROVAL",
            "계정 승인 대기 중입니다."
    ),

    ADMIN_REJECTED(
            HttpStatus.FORBIDDEN,
            "ADMIN_REJECTED",
            "계정 신청이 거부되었습니다."
    ),

    ADMIN_SUSPENDED(
            HttpStatus.FORBIDDEN,
            "ADMIN_SUSPENDED",
            "정지된 계정입니다."
    ),

    ADMIN_INACTIVE(
            HttpStatus.FORBIDDEN,
            "ADMIN_INACTIVE",
            "비활성화된 계정입니다."
    ),
    INVALID_PASSWORD(
            HttpStatus.FORBIDDEN,
            "INVALID_PASSWORD",
            "현재 비밀번호가 올바르지 않습니다."
    ),

    // 404 NOT_FOUND
    INVALID_CREDENTIALS(
            HttpStatus.NOT_FOUND,
            "INVALID_CREDENTIALS",
            "이메일 또는 비밀번호가 올바르지 않습니다."
    ),
    ADMIN_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ADMIN_NOT_FOUND",
            "관리자가 존재하지 않습니다."
    ),
    CUSTOMER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CUSTOMER_NOT_FOUND",
            "고객이 존재하지 않습니다."
    ),
    PRODUCT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PRODUCT_NOT_FOUND",
            "상품이 존재하지 않습니다."
    ),
    ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ORDER_NOT_FOUND",
            "주문이 존재하지 않습니다."
    ),
    REVIEW_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "REVIEW_NOT_FOUND",
            "리뷰가 존재하지 않습니다."
    ),

    // 409 CONFLICT
    EMAIL_DUPLICATE(
            HttpStatus.CONFLICT,
            "EMAIL_DUPLICATE",
            "이미 사용 중인 이메일입니다."
    ),
    INSUFFICIENT_STOCK(
            HttpStatus.CONFLICT,
            "INSUFFICIENT_STOCK",
            "재고가 부족합니다."
    ),
    INVALID_ORDER_STATUS_TRANSITION(
            HttpStatus.CONFLICT,
            "INVALID_ORDER_STATUS_TRANSITION",
            "허용되지 않는 주문 상태 변경입니다."
    ),
    ORDER_CANCEL_NOT_ALLOWED(
            HttpStatus.CONFLICT,
            "ORDER_CANCEL_NOT_ALLOWED",
            "이미 취소되었거나 취소할 수 없는 주문입니다."
    ),
    /**
     * 슈퍼 관리자 삭제 시도 시 (시스템 보호)
     */
    CANNOT_DELETE_SUPER_ADMIN(
            HttpStatus.CONFLICT,
            "CANNOT_DELETE_SUPER_ADMIN",
            "시스템 보호를 위해 슈퍼 관리자 계정은 삭제할 수 없습니다."
    ),
    /**
     * 활성 상태인 관리자 삭제 시도 시 (운영 실수 방지)
     */
    CANNOT_DELETE_ACTIVE_ADMIN(
            HttpStatus.CONFLICT,
            "CANNOT_DELETE_ACTIVE_ADMIN",
            "활동 중인 관리자 계정은 삭제할 수 없습니다. 정지 후 다시 시도해주세요."
    ),
    /**
     * 승인 대기 중인 계정 삭제 시도 시 (프로세스 유지)
     */
    CANNOT_DELETE_PENDING_ADMIN(
            HttpStatus.CONFLICT,
            "CANNOT_DELETE_PENDING_ADMIN",
            "승인 대기 중인 계정은 삭제할 수 없습니다. 삭제 대신 거절 처리를 해주세요."
    ),
    /**
     * 거절된 계정 삭제 시도 시 (기록 보관 정책)
     */
    CANNOT_DELETE_REJECTED_ADMIN(
            HttpStatus.CONFLICT,
            "CANNOT_DELETE_REJECTED_ADMIN",
            "거절된 계정은 보안 기록 유지를 위해 즉시 삭제할 수 없습니다."
    ),
    /**
     * 이미 승인 또는 거절 처리가 완료된 관리자에게 재요청 시 (409 Conflict)
     */
    ALREADY_PROCESSED_ADMIN(
            HttpStatus.CONFLICT,
            "ALREADY_PROCESSED_ADMIN",
            "이미 승인 또는 거절 처리가 완료된 관리자입니다."
    ),
    CUSTOMER_DELETE_NOT_ALLOWED(
            HttpStatus.CONFLICT,
            "CUSTOMER_DELETE_NOT_ALLOWED",
            "삭제할 수 없는 고객 상태입니다."
    ),
    PRODUCT_DELETE_NOT_ALLOWED(
            HttpStatus.CONFLICT,
            "PRODUCT_DELETE_NOT_ALLOWED",
            "주문 또는 리뷰가 연결된 상품은 삭제할 수 없습니다."
    ),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    /**
     * HTTP 상태 코드 숫자 반환
     */
    public int getStatus() {
        return httpStatus.value();
    }
}
