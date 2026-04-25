package com.ildang100.backoffice.common.execption;

import lombok.Getter;


/**
 * <p>서비스 계층에서 발생하는 비즈니스 예외를 표현하는 커스텀 예외 클래스입니다.</p>
 *
 * <p>
 * {@link ErrorCode}를 기반으로 예외를 관리하며,
 * 예외 발생 시 클라이언트에게 일관된 에러 응답을 제공하기 위해 사용됩니다.
 * </p>
 *
 * <p><b>동작 방식</b></p>
 * <ul>
 *     <li>{@code ErrorCode}를 전달받아 예외 생성</li>
 *     <li>부모 클래스({@link RuntimeException})의 메시지로 ErrorCode의 메시지 설정</li>
 *     <li>전역 예외 처리기에서 ErrorCode를 기반으로 응답 생성</li>
 * </ul>
 *
 * <p><b>사용 예시</b></p>
 * <pre>
 * {@code
 * if (user == null) {
 *     throw new ServiceException(ErrorCode.USER_NOT_FOUND);
 * }
 * }
 * </pre>
 *
 * <p><b>주의 사항</b></p>
 * <ul>
 *     <li>비즈니스 로직에서 발생하는 예외만 처리하며, 시스템 예외와는 구분하여 사용합니다.</li>
 * </ul>
 *
 * @author 이우람
 * @since 2026-04-25
 */
@Getter
public class ServiceException extends RuntimeException {
    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
