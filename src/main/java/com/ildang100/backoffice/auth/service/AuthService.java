package com.ildang100.backoffice.auth.service;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.service.AdminService;
import com.ildang100.backoffice.auth.dto.AdminLoginRequest;
import com.ildang100.backoffice.auth.dto.AdminLoginResponse;
import com.ildang100.backoffice.auth.dto.AdminSignUpRequest;
import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.jwt.JwtProvider;
import com.ildang100.backoffice.auth.session.SessionConst;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자 로그인 처리
 *
 * <p>
 * 이메일과 비밀번호를 검증한 후 JWT Access Token을 발급합니다.
 * </p>
 *
 * <p><b>처리 흐름</b></p>
 * <ol>
 *     <li>이메일 기반 관리자 조회</li>
 *     <li>비밀번호 검증</li>
 *     <li>계정 상태 검증 (로그인 가능 여부)</li>
 *     <li>JWT Access Token 생성</li>
 * </ol>
 *
 * <p>
 * 기존 세션 기반 인증 대신, 클라이언트는 발급된 토큰을
 * Authorization 헤더에 담아 이후 요청을 수행해야 합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 관리자 회원가입 처리
     *
     * <p>
     * 요청받은 정보를 기반으로 새로운 관리자 계정을 생성합니다.
     * </p>
     *
     * <p><b>처리 흐름</b></p>
     * <ol>
     *     <li>이메일 중복 여부 검증</li>
     *     <li>비밀번호 암호화</li>
     *     <li>Admin 엔티티 생성</li>
     *     <li>DB 저장</li>
     * </ol>
     *
     * <p>
     * 생성된 관리자는 기본적으로 {@code PENDING_APPROVAL} 상태로 저장되며,
     * 이후 별도의 승인 과정을 거쳐야 활성화됩니다.
     * </p>
     *
     * @param request 관리자 회원가입 요청 DTO
     * @throws ServiceException 이메일 중복 시 발생
     */
    @Transactional
    public void signUp(AdminSignUpRequest request) {
        adminService.createAdmin(request);
    }

    @Transactional
    public AdminLoginResponse  login(AdminLoginRequest request, HttpSession session) {
        Admin admin = adminService.getByEmail(request.getEmail());

        validatePassword(request.getPassword(), admin.getPassword());

        admin.validateLoginAvailable();

        // TODO(auth-jwt): JWT 전환 기간 동안만 기존 세션 로그인을 함께 유지한다.
        // 모든 보호 API가 Authorization Bearer 토큰으로 검증되면 이 세션 저장 로직을 제거한다.
        session.setAttribute(
                SessionConst.LOGIN_ADMIN,
                LoginAdminDto.from(admin)
        );

        String accessToken = jwtProvider.createAccessToken(admin);

        return new AdminLoginResponse(accessToken);
    }

    /**
     * 관리자 로그아웃 처리
     *
     * <p>
     * 현재 요청의 {@link HttpSession}을 무효화하여
     * 세션에 저장된 로그인 관리자 정보를 제거합니다.
     * </p>
     *
     * @param session 현재 HTTP 세션
     */
    @Transactional
    public void logout(HttpSession session) {
        // TODO(auth-jwt): JWT 전용 인증으로 전환되면 로그아웃은 클라이언트 토큰 삭제 정책으로 변경한다.
        // Refresh Token을 도입하면 서버 저장소에서 refresh token을 폐기하는 방식으로 확장한다.
        session.invalidate();
    }

    private void validatePassword(String raw, String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) {
            throw new ServiceException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
