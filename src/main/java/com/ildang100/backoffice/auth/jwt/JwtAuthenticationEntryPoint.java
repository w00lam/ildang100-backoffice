package com.ildang100.backoffice.auth.jwt;

import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 인증 실패 응답을 JSON 형태로 내려주는 진입점입니다.
 *
 * <p>
 * 인증이 필요한 API에 토큰 없이 접근하거나, JWT 필터에서 토큰 관련 오류를 request attribute에 남긴 경우
 * Spring Security가 이 클래스를 호출합니다.
 * </p>
 *
 * <p>
 * 예를 들어 토큰이 없으면 TOKEN_REQUIRED, 만료되었으면 TOKEN_EXPIRED,
 * 서명이 다르면 INVALID_TOKEN_SIGNATURE 응답을 내려줍니다.
 * </p>
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        // 필터에서 별도 에러를 남기지 않았다면 토큰이 없는 인증 실패로 처리합니다.
        if (errorCode == null) {
            errorCode = ErrorCode.TOKEN_REQUIRED;
        }

        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), CommonApiResponse.error(errorCode));
    }
}
