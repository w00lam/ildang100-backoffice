package com.ildang100.backoffice.auth.service;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.auth.dto.AdminLoginRequest;
import com.ildang100.backoffice.auth.dto.AdminSignUpRequest;
import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.session.SessionConst;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.config.PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ildang100.backoffice.common.enums.AdminStatus.ACTIVE;

/**
 * 관리자 인증 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>
 * 관리자 회원가입을 포함한 인증 관련 기능을 담당하며,
 * 데이터 검증 및 엔티티 생성, 저장 로직을 수행합니다.
 * </p>
 *
 * <p><b>주요 기능</b></p>
 * <ul>
 *     <li>관리자 회원가입 처리</li>
 *     <li>이메일 중복 검증</li>
 *     <li>비밀번호 암호화</li>
 * </ul>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
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
        this.validateDuplicateEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Admin admin = Admin.create(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getTele(),
                request.getRole()
        );

        adminRepository.save(admin);
    }

    /**
     * 관리자 로그인 처리
     *
     * <p><b>처리 흐름</b></p>
     * <ol>
     *     <li>이메일로 관리자 조회</li>
     *     <li>비밀번호 검증</li>
     *     <li>계정 상태 확인</li>
     *     <li>세션에 로그인 정보 저장</li>
     * </ol>
     *
     * @param request 로그인 요청 DTO
     * @param session HttpSession
     */
    @Transactional
    public void login(AdminLoginRequest request, HttpSession session) {

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new ServiceException(ErrorCode.INVALID_CREDENTIALS);
        }

        validateLoginAvailable(admin);

        session.setAttribute(SessionConst.LOGIN_ADMIN, LoginAdminDto.from(admin));
    }

    /**
     * 이메일 중복 여부 검증
     *
     * <p>
     * 동일한 이메일을 가진 관리자 계정이 이미 존재하는 경우 예외를 발생시킵니다.
     * </p>
     *
     * @param email 확인할 이메일
     * @throws ServiceException 이메일이 이미 존재하는 경우
     */
    private void validateDuplicateEmail(String email) {
        if (adminRepository.existsByEmail(email)) {
            throw new ServiceException(ErrorCode.EMAIL_DUPLICATE);
        }
    }

    /**
     * 관리자 로그인 가능 여부를 검증합니다.
     *
     * <p>
     * 관리자 계정의 상태(status)를 기반으로 로그인 가능 여부를 판단합니다.
     * ACTIVE 상태인 경우에만 로그인이 허용되며,
     * 그 외 상태는 각각의 상황에 맞는 예외를 발생시킵니다.
     * </p>
     *
     * <p><b>처리 흐름</b></p>
     * <ul>
     *     <li>ACTIVE: 로그인 허용</li>
     *     <li>그 외 상태: 예외 발생</li>
     * </ul>
     *
     * <p><b>상태별 예외</b></p>
     * <ul>
     *     <li>PENDING_APPROVAL: 승인 대기 → 로그인 불가</li>
     *     <li>REJECTED: 승인 거부 → 로그인 불가</li>
     *     <li>SUSPENDED: 계정 정지 → 로그인 불가</li>
     *     <li>INACTIVE: 계정 비활성 → 로그인 불가</li>
     * </ul>
     *
     * @param admin 관리자 엔티티
     * @throws ServiceException 계정 상태에 따른 예외 발생
     */
    private void validateLoginAvailable(Admin admin) {

        if (admin.getStatus() == ACTIVE) {
            return;
        }

        switch (admin.getStatus()) {
            case PENDING_APPROVAL -> throw new ServiceException(ErrorCode.ADMIN_PENDING_APPROVAL);
            case REJECTED -> throw new ServiceException(ErrorCode.ADMIN_REJECTED);
            case SUSPENDED -> throw new ServiceException(ErrorCode.ADMIN_SUSPENDED);
            case INACTIVE -> throw new ServiceException(ErrorCode.ADMIN_INACTIVE);
            default -> throw new ServiceException(ErrorCode.INVALID_ADMIN_STATUS);
        }
    }
}
