package com.ildang100.backoffice.admin.service;

import com.ildang100.backoffice.admin.dto.*;
import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.entity.AdminApprovalHistory;
import com.ildang100.backoffice.admin.repository.AdminApprovalHistoryRepository;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminApprovalHistoryRepository approvalHistoryRepository;

    /**
     * 조건에 맞는 관리자 목록 페이징 조회 로직
     *
     * <p>
     * 컨트롤러로부터 전달받은 검색 및 페이징 파라미터를 기반으로 DB에서 관리자 데이터를 조회합니다.
     * </p>
     *
     * @param keyword 검색어 (이름 또는 이메일)
     * @param role 관리자 권한 (Enum 문자열)
     * @param status 관리자 계정 상태 (Enum 문자열)
     * @param page 요청 페이지 번호 (1부터 시작)
     * @param size 페이지당 데이터 개수
     * @param sortBy 정렬 기준 필드명
     * @param sortOrder 정렬 방향 (asc/desc)
     * @return 페이징 메타 정보와 관리자 응답 DTO 리스트가 포함된 PageResponse 객체
     *
     * @author 박채빈
     * @since 2026-04-27
     */
    @Transactional(readOnly = true)
    public AdminListResponse getAdminList(
            String keyword, AdminRole role, AdminStatus status,
            int page, int size, String sortBy, String sortOrder) {

        // 1. 페이지 번호 변환 (클라이언트 1-based -> JPA 0-based)
        int pageNumber = Math.max(0, page - 1);

        // 2. 정렬 조건 설정
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
     * @param adminId 조회할 관리자의 고유 ID
     * @return 관리자 응답 DTO
     * @throws ServiceException 관리자를 찾을 수 없는 경우 발생
     */
    public AdminResponse getAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

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
        // 1. 대상 조회
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        // 2. 이메일 중복 체크 (본인 이메일이 아닌데 이미 존재하는 경우)
        if (!admin.getEmail().equals(request.getEmail()) &&
                adminRepository.existsByEmail(request.getEmail())) {
            throw new ServiceException(ErrorCode.EMAIL_DUPLICATE);
        }

        // 3. 정보 업데이트 (Entity에 update 메서드가 있다고 가정)
        admin.update(
                request.getName(),
                request.getEmail(),
                request.getTele()
        );

        return AdminResponse.from(admin);
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
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

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
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        admin.updateStatus(request.getStatus());

        return AdminResponse.from(admin);
    }
    /**
     * 관리자 삭제 비즈니스 로직
     *
     * <p><b>처리 흐름</b></p>
     * <ol>
     * <li>대상 관리자 존재 여부 확인</li>
     * <li>삭제 불가능한 상태(예: 이미 삭제 처리된 경우 등)인지 검증</li>
     * <li>Repository를 통한 데이터 삭제 실행</li>
     * </ol>
     *
     * @param adminId 삭제할 관리자 고유 ID
     * @throws ServiceException 관리자가 없거나(ADMIN_NOT_FOUND), 삭제할 수 없는 상태일 때 발생
     */
    @Transactional
    public void deleteAdmin(Long adminId) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

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

        admin.updateStatus(AdminStatus.INACTIVE);
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
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        if (admin.getStatus() != AdminStatus.PENDING_APPROVAL) {
            throw new ServiceException(ErrorCode.ALREADY_PROCESSED_ADMIN);
        }

        if (!request.getIsApproved() && (request.getRejectReason() == null || request.getRejectReason().isBlank())) {
            throw new ServiceException(ErrorCode.REJECT_REASON_REQUIRED);
        }

        LocalDateTime now = LocalDateTime.now();
        AdminStatus targetStatus = request.getIsApproved() ? AdminStatus.ACTIVE : AdminStatus.REJECTED;

        if (request.getIsApproved()) {
            admin.approve(now);
        } else {
            admin.reject();
        }

        AdminApprovalHistory history = AdminApprovalHistory.builder()
                .adminId(adminId)
                .status(targetStatus)
                .rejectReason(request.getRejectReason())
                .rejectedAt(request.getIsApproved() ? null : now) // 거절일 때만 rejectedAt 설정
                .build();

        approvalHistoryRepository.save(history);

        return AdminApprovalResponse.from(admin, request.getRejectReason(), now);
    }

    /**
     * 현재 로그인한 관리자의 프로필 정보를 조회합니다.
     *
     * <p>조회용 메서드이므로 성능 최적화를 위해 readOnly = true를 적용합니다.</p>
     *
     * @param adminId 조회할 관리자의 고유 ID (세션에서 추출)
     * @return 관리자 프로필 정보 응답 DTO
     * @throws ServiceException 관리자 정보를 찾을 수 없을 때 발생
     */
    @Transactional(readOnly = true)
    public AdminResponse getAdminProfile(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        return AdminResponse.from(admin);
    }

    /**
     * 관리자 프로필 수정 로직
     * * @param adminId 세션에서 추출한 관리자 ID
     * @param request 수정할 프로필 정보
     * @return 수정 완료된 프로필 응답 객체
     */
    @Transactional
    public AdminResponse updateAdminProfile(Long adminId, AdminProfileUpdateRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));

        this.validateDuplicateEmail(request.getEmail());

        admin.update(
                request.getName(),
                request.getEmail(),
                request.getTele()
        );

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
}
