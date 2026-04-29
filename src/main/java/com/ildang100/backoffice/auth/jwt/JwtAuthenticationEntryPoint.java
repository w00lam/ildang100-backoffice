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
 * 인증 실패 시 처리 로직을 담당하는 클래스입니다.
 *
 * <p>
 * 인증이 필요한 요청에서 JWT 토큰이 없거나 유효하지 않을 경우 호출됩니다.
 * </p>
 *
 * <p><b>동작 방식</b></p>
 * <ul>
 *     <li>필터에서 전달된 에러 코드를 request attribute에서 추출</li>
 *     <li>해당 에러 코드 기반으로 HTTP 상태 및 메시지 설정</li>
 *     <li>JSON 형식의 공통 에러 응답 반환</li>
 * </ul>
 *
 * <p>
 * 필터에서 발생한 인증 관련 예외를 중앙에서 일관되게 처리하기 위한 진입점입니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
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

        if (errorCode == null) {
            errorCode = ErrorCode.TOKEN_REQUIRED;
        }

        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), CommonApiResponse.error(errorCode));
    }
}
