package com.ildang100.backoffice.auth.util;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * 인증된 관리자 정보를 조회하는 유틸 클래스입니다.
 *
 * <p>
 * SecurityContext에 저장된 Authentication 객체를 기반으로
 * 현재 로그인한 관리자 정보를 반환합니다.
 * </p>
 *
 * <p>
 * 인증 정보가 없거나 타입이 일치하지 않을 경우 예외를 발생시킵니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
public class AuthUtils {

    private AuthUtils() {
    }

    public static LoginAdminDto getLoginAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof LoginAdminDto loginAdmin)) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED);
        }

        return loginAdmin;
    }
}
