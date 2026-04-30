package com.ildang100.backoffice.auth.jwt;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.common.enums.DeletionStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
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
 * 요청마다 JWT 토큰을 검사하고 Spring Security 인증 정보로 변환하는 필터입니다.
 *
 * <p>
 * 클라이언트는 보호 API 호출 시 {@code Authorization: Bearer {token}} 형식으로 토큰을 전달합니다.
 * 이 필터는 헤더에서 토큰을 꺼내 검증하고, 유효한 토큰이면 SecurityContext에 로그인 관리자 정보를 저장합니다.
 * </p>
 *
 * <p>
 * 여기서 SecurityContext에 저장된 인증 정보는 이후 컨트롤러의 {@code AuthUtils.getLoginAdmin()}이나
 * Security 인가 설정({@code hasRole}, {@code hasAnyRole})에서 사용됩니다.
 * </p>
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

            Admin admin = adminRepository.findByIdAndDeletionStatus(adminId, DeletionStatus.NOT_DELETED)
                    .orElse(null);

            if (admin == null) {
                request.setAttribute("exception", ErrorCode.INVALID_TOKEN);
                filterChain.doFilter(request, response);
                return;
            }

            try {
                admin.validateLoginAvailable();
            } catch (ServiceException e) {
                request.setAttribute("exception", e.getErrorCode());
                filterChain.doFilter(request, response);
                return;
            }

            LoginAdminDto loginAdmin = LoginAdminDto.from(admin);

            // Spring Security는 ROLE_ 접두사가 붙은 권한을 기준으로 hasRole, hasAnyRole을 판단합니다.
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
        } catch (SignatureException e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN_SIGNATURE);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Authorization 헤더에서 Bearer 토큰만 추출합니다.
     *
     * <p>
     * 헤더가 없으면 비로그인 요청으로 보고 다음 필터로 넘깁니다.
     * 보호 API라면 이후 Spring Security가 {@link JwtAuthenticationEntryPoint}를 호출해 TOKEN_REQUIRED를 응답합니다.
     * </p>
     */
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
