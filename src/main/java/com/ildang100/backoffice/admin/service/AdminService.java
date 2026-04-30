package com.ildang100.backoffice.admin.service;

import com.ildang100.backoffice.admin.dto.*;
import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.entity.AdminApprovalHistory;
import com.ildang100.backoffice.admin.repository.AdminApprovalHistoryRepository;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.auth.dto.AdminSignUpRequest;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.enums.DeletionStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminApprovalHistoryRepository approvalHistoryRepository;

    /**
     * 관리자 계정을 생성합니다.
     *
     * <p>
     * 관리자 회원가입 시 호출되는 메서드로,
     * 이메일 중복 검증 → 관리자 엔티티 생성(비밀번호 암호화 위임) → DB 저장
     * 순서로 흐름을 제어합니다.
     * </p>
     *
     * <p>
     * 객체지향적 설계(Tell, Don't Ask)에 따라 서비스 레이어에서 직접 비밀번호를 암호화하지 않습니다.
     * 대신 {@link org.springframework.security.crypto.password.PasswordEncoder}를
     * 엔티티의 팩토리 메서드로 전달하여, 객체 스스로 평문 비밀번호를 암호화하도록 책임을 위임합니다.
     * </p>
     *
     * <p>
     * 가입 전 동일한 이메일을 가진 계정이 이미 존재할 경우,
     * 중복 가입을 방지하기 위해 예외를 발생시킵니다.
     * </p>
     *
     * @param request 관리자 회원가입 요청 DTO
     * @throws ServiceException 이메일이 이미 존재하는 경우 (DUPLICATE_EMAIL)
     */
    @Transactional
    public void createAdmin(AdminSignUpRequest request) {

        this.validateDuplicateEmail(request.getEmail());

        Admin admin = Admin.create(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getTele(),
                request.getRole(),
                passwordEncoder
        );

        adminRepository.save(admin);
    }

    /**
     * 조건에 맞는 관리자 목록 페이징 조회 로직
     *
     * <p>
     * 컨트롤러로부터 전달받은 검색 및 페이징 파라미터를 기반으로 DB에서 관리자 데이터를 조회합니다.
     * </p>
     *
     * @param keyword   검색어 (이름 또는 이메일)
     * @param role      관리자 권한 (Enum 문자열)
     * @param status    관리자 계정 상태 (Enum 문자열)
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param sortBy    정렬 기준 필드명
     * @param sortOrder 정렬 방향 (asc/desc)
     * @return 페이징 메타 정보와 관리자 응답 DTO 리스트가 포함된 PageResponse 객체
     * @author 박채빈
     * @since 2026-04-27
     */
    @Transactional(readOnly = true)
    public AdminListResponse getAdminList(
            String keyword, AdminRole role, AdminStatus status,
            int page, int size, String sortBy, String sortOrder) {

        // 1. 페이지 번호 변환 (클라이언트 1-based -> JPA 0-based)
        int pageNumber = Math.max(0, page - 1);

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, sortBy);
        }

        Pageable pageable = PageRequest.of(pageNumber, size, sort);

        Page<Admin> adminPage = adminRepository.findAdminsByCondition(keyword, role, status, pageable);

        return AdminListResponse.from(adminPage);
    }

    /**
     * 관리자 단건 조회
     *
     * @param adminId 조회할 관리자의 고유 ID
     * @return 관리자 응답 DTO
     * @throws ServiceException 관리자를 찾을 수 없는 경우 발생
     */
    public AdminResponse getAdmin(Long adminId) {

        Admin admin = adminRepository.getById(adminId);

        return AdminResponse.from(admin);
    }

    /**
     * 관리자 정보 수정 비즈니스 로직
     *
     * <p><b>처리 흐름</b></p>
     * <ol>
     * <li>ID 기반 관리자 존재 여부 확인</li>
     * <li>이메일 변경 시, 타 계정과의 이메일 중복 검증</li>
     * <li>엔티티 내부 update 메서드 호출을 통한 정보 갱신</li>
     * </ol>
     *
     * @param adminId 수정 대상 관리자 고유 ID
     * @param request 수정할 데이터 DTO
     * @return 수정 완료된 관리자 응답 DTO
     * @throws ServiceException 관리자가 없거나(ADMIN_NOT_FOUND), 이메일이 중복된 경우(EMAIL_DUPLICATE) 발생
     */
    @Transactional
    public AdminResponse updateAdmin(Long adminId, AdminInfoUpdateRequest request) {

        Admin admin = adminRepository.getById(adminId);

        this.validateDuplicateEmail(request.getEmail());

        admin.update(request);

        return AdminResponse.from(admin);
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
     * 관리자 권한 역할 변경 비즈니스 로직
     *
     * <p><b>처리 흐름</b></p>
     * <ol>
     * <li>대상 관리자 존재 여부 확인</li>
     * <li>엔티티의 역할 정보 업데이트</li>
     * </ol>
     *
     * @param adminId 수정 대상 관리자 고유 ID
     * @param request 변경할 역할 정보가 담긴 DTO
     * @return 수정 완료된 관리자 응답 DTO
     * @throws ServiceException 관리자를 찾을 수 없는 경우(ADMIN_NOT_FOUND) 발생
     */
    @Transactional
    public AdminResponse updateAdminRole(Long adminId, AdminRoleUpdateRequest request) {

        Admin admin = adminRepository.getById(adminId);

        admin.updateRole(request.getRole());

        return AdminResponse.from(admin);
    }

    /**
     * 관리자 상태 변경 비즈니스 로직
     *
     * <p><b>처리 흐름</b></p>
     * <ol>
     * <li>ID 기반으로 변경 대상 관리자 존재 여부 확인</li>
     * <li>엔티티 내부 메서드 호출을 통한 상태값 갱신</li>
     * </ol>
     *
     * @param adminId 수정 대상 관리자 고유 ID
     * @param request 변경할 상태 정보를 담은 DTO
     * @return 수정 완료된 관리자 응답 DTO
     * @throws ServiceException 관리자를 찾을 수 없는 경우(ADMIN_NOT_FOUND) 발생
     */
    @Transactional
    public AdminResponse updateAdminStatus(Long adminId, AdminStatusUpdateRequest request) {

        Admin admin = adminRepository.getById(adminId);

        admin.updateStatus(request.getStatus());

        return AdminResponse.from(admin);
    }

    /**
     * 관리자를 소프트 삭제 처리합니다.
     *
     * <p>슈퍼 관리자, 활성 관리자, 승인 대기/거절 관리자는 삭제할 수 없고,
     * 삭제 가능한 관리자만 {@code deletionStatus}를 {@code DELETED}로 변경합니다.</p>
     *
     * @param adminId 삭제 처리할 관리자 ID
     * @throws ServiceException 관리자를 찾을 수 없거나, 이미 삭제되었거나, 삭제할 수 없는 상태인 경우
     */
    @Transactional
    public void deleteAdmin(Long adminId) {

        Admin admin = adminRepository.getByIdIncludingDeleted(adminId);

        if (admin.isDeleted()) {
            admin.markAsDeleted();
        }

        // 1. 슈퍼 관리자 삭제 방지
        if (admin.getRole() == AdminRole.SUPER_ADMIN) {
            throw new ServiceException(ErrorCode.CANNOT_DELETE_SUPER_ADMIN);
        }

        // 2. 활동 중인 계정 삭제 방지
        if (admin.getStatus() == AdminStatus.ACTIVE) {
            throw new ServiceException(ErrorCode.CANNOT_DELETE_ACTIVE_ADMIN);
        }

        // 3. 승인 대기 중인 계정 삭제 방지
        if (admin.getStatus() == AdminStatus.PENDING_APPROVAL) {
            throw new ServiceException(ErrorCode.CANNOT_DELETE_PENDING_ADMIN);
        }

        // 4. 거절된 계정 삭제 방지 (기록 보관 정책)
        if (admin.getStatus() == AdminStatus.REJECTED) {
            throw new ServiceException(ErrorCode.CANNOT_DELETE_REJECTED_ADMIN);
        }

        admin.markAsDeleted();
    }

    /**
     * 관리자 가입 승인/거절 처리 및 이력 기록
     *
     * <p>승인 시 관리자 엔티티의 {@code approvedAt}을 업데이트하고,
     * 거절 시 이력 엔티티의 {@code rejectedAt}을 업데이트합니다.</p>
     *
     * @param adminId 처리 대상 관리자 PK
     * @param request 승인 여부 및 사유
     * @return 각 상태에 맞는 날짜가 포함된 응답 DTO
     */
    @Transactional
    public AdminApprovalResponse approveAdmin(Long adminId, AdminApprovalRequest request) {

        Admin admin = adminRepository.getById(adminId);

        LocalDateTime now = LocalDateTime.now();

        if (request.getIsApproved()) {
            admin.approve(now);
        } else {
            validateRejectReason(request.getRejectReason());
            admin.reject();

            AdminApprovalHistory history = AdminApprovalHistory.createRejection(adminId, request.getRejectReason(), now);
            approvalHistoryRepository.save(history);
        }

        return AdminApprovalResponse.from(admin, request.getRejectReason(), now);
    }

    /**
     * 거절 사유 필수값 검증
     */
    private void validateRejectReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new ServiceException(ErrorCode.REJECT_REASON_REQUIRED);
        }
    }

    /**
     * 현재 로그인한 관리자의 프로필 정보를 조회합니다.
     *
     * <p>조회용 메서드이므로 성능 최적화를 위해 readOnly = true를 적용합니다.</p>
     *
     * @param adminId 조회할 관리자의 고유 ID
     * @return 관리자 프로필 정보 응답 DTO
     * @throws ServiceException 관리자 정보를 찾을 수 없을 때 발생
     */
    @Transactional(readOnly = true)
    public AdminResponse getAdminProfile(Long adminId) {

        Admin admin = adminRepository.getById(adminId);

        return AdminResponse.from(admin);
    }

    /**
     * 관리자 프로필 수정 로직
     * @param adminId 현재 로그인한 관리자 ID
     * @param request 수정할 프로필 정보
     * @return 수정 완료된 프로필 응답 객체
     */
    @Transactional
    public AdminResponse updateAdminProfile(Long adminId, AdminInfoUpdateRequest request) {

        Admin admin = adminRepository.getById(adminId);

        this.validateDuplicateEmailAndId(request.getEmail(), admin);

        admin.update(request);

        return AdminResponse.from(admin);
    }

    /**
     * 이메일 중복 여부 검증(본인 제외)
     *
     * <p>
     * 자신을 제외한 동일한 이메일을 가진 관리자 계정이 이미 존재하는 경우 예외를 발생시킵니다.
     * </p>
     *
     * @param newEmail 확인할 새로운 이메일
     * @param admin 이메일 바꿀 관리자
     * @throws ServiceException 이메일이 이미 존재하는 경우
     */
    private void validateDuplicateEmailAndId(String newEmail, Admin admin) {
        if (StringUtils.hasText(newEmail) && !newEmail.equals(admin.getEmail())
                && adminRepository.existsByEmailAndIdNot(newEmail, admin.getId())) {
            throw new ServiceException(ErrorCode.EMAIL_DUPLICATE);
        }
    }

    /**
     * 관리자 비밀번호 변경 로직
     */
    @Transactional
    public void updatePassword(Long adminId, AdminPasswordUpdateRequest request) {

        Admin admin = adminRepository.getById(adminId);

        validateNewPasswordConfirm(request.getNewPassword(), request.getNewPasswordConfirm());

        admin.changePassword(request.getCurrentPassword(), request.getNewPassword(), passwordEncoder);
    }

    /**
     * 새 비밀번호와 확인용 비밀번호 일치 여부 검증
     */
    private void validateNewPasswordConfirm(String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new ServiceException(ErrorCode.PASSWORD_NEW_CONFIRM_MISMATCH);
        }
    }

    /**
     * 이메일을 기준으로 관리자를 조회합니다.
     *
     * <p>
     * 로그인 과정에서 사용자 인증을 위해 사용되는 메서드입니다.
     * 전달받은 이메일에 해당하는 관리자가 존재하지 않을 경우,
     * 보안상 이유로 "존재하지 않는 계정"과 "비밀번호 불일치"를 구분하지 않고
     * 동일한 예외를 발생시킵니다.
     * </p>
     *
     * <p>
     * 즉, 계정 존재 여부를 외부에 노출하지 않기 위해
     * {@link ErrorCode#INVALID_CREDENTIALS}를 사용합니다.
     * </p>
     *
     * @param email 조회할 관리자 이메일
     * @return 조회된 관리자 엔티티
     * @throws ServiceException 이메일에 해당하는 관리자가 존재하지 않는 경우
     */
    public Admin getByEmail(String email) {
        return adminRepository.findByEmailAndDeletionStatus(email, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_CREDENTIALS));
    }

    /**
     * 관리자 ID로 관리자를 조회하고, 없으면 예외를 발생시킵니다.
     *
     * <p>SecurityContext에서 얻은 관리자 ID 검증에도 사용하므로, 조회 실패 시 인증 실패 응답으로 처리합니다.</p>
     *
     * @param adminId 조회할 관리자 ID
     * @return 조회된 관리자 엔티티
     * @throws ServiceException 관리자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Admin getAdminOrThrow(Long adminId) {
        return adminRepository.findByIdAndDeletionStatus(adminId, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ServiceException(ErrorCode.UNAUTHORIZED));
    }
}
