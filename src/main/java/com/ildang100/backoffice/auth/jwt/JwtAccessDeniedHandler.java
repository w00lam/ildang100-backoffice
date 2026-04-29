package com.ildang100.backoffice.auth.jwt;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 인증은 되었지만 권한이 부족한 경우의 응답을 처리합니다.
 *
 * <p>
 * 예를 들어 CS_ADMIN 토큰으로 SUPER_ADMIN 전용 API에 접근하면
 * 인증 자체는 성공했지만 권한이 부족하므로 이 핸들러가 403 FORBIDDEN 응답을 내려줍니다.
 * </p>
 */
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;

        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), CommonApiResponse.error(errorCode));
    }
}
