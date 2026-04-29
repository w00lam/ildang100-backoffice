package com.ildang100.backoffice.auth.jwt;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증을 처리하는 필터입니다.
 *
 * <p>
 * 모든 요청에 대해 Authorization 헤더의 JWT 토큰을 검사하고,
 * 유효한 경우 사용자 정보를 조회하여 SecurityContext에 인증 정보를 설정합니다.
 * </p>
 *
 * <p><b>처리 흐름</b></p>
 * <ol>
 *     <li>Authorization 헤더에서 Bearer 토큰 추출</li>
 *     <li>JWT 토큰 유효성 검증</li>
 *     <li>토큰에서 사용자 ID 추출</li>
 *     <li>DB에서 사용자 조회</li>
 *     <li>Authentication 객체 생성 및 SecurityContext에 저장</li>
 * </ol>
 *
 * <p>
 * 토큰이 없거나 잘못된 경우, 필터는 예외를 직접 처리하지 않고
 * request attribute에 에러 정보를 담은 뒤 다음 필터로 전달합니다.
 * 이후 JwtAuthenticationEntryPoint에서 응답을 처리합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final AdminRepository adminRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, AdminRepository adminRepository) {
        this.jwtProvider = jwtProvider;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long adminId = jwtProvider.getAdminId(token);

            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

            LoginAdminDto loginAdmin = LoginAdminDto.from(admin);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            loginAdmin,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + loginAdmin.getRole().name()))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", ErrorCode.TOKEN_EXPIRED);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN);
            filterChain.doFilter(request, response);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization == null) {
            return null;
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN_FORMAT);
            return null;
        }

        return authorization.substring(BEARER_PREFIX.length());
    }
}
