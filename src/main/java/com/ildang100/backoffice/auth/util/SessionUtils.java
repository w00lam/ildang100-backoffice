package com.ildang100.backoffice.auth.util;

import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.session.SessionConst;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import jakarta.servlet.http.HttpSession;

public final class SessionUtils {

    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자
     */
    private SessionUtils() {
    }

    /**
     * 현재 세션에서 로그인된 관리자 정보를 조회합니다.
     *
     * <p>
     * 세션에 로그인 정보가 존재하지 않을 경우 UNAUTHORIZED 예외를 발생시킵니다.
     * </p>
     *
     * @param session HttpSession
     * @return 로그인된 관리자 정보
     * @throws ServiceException 로그인 정보가 없는 경우
     *
     * @author 이우람
     * @since 2026-04-26
     */
    public static LoginAdminDto getLoginAdmin(HttpSession session) {
        LoginAdminDto loginAdmin = (LoginAdminDto) session.getAttribute(SessionConst.LOGIN_ADMIN);

        if (loginAdmin == null) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED);
        }

        return loginAdmin;
    }

    /**
     * 로그인 여부를 확인합니다.
     *
     * @param session HttpSession
     * @return 로그인 상태 여부 (true: 로그인됨, false: 로그인 안됨)
     */
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SessionConst.LOGIN_ADMIN) != null;
    }
}