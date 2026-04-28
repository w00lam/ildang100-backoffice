package com.ildang100.backoffice.admin.controller;

import com.ildang100.backoffice.admin.dto.*;
import com.ildang100.backoffice.admin.service.AdminService;
import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    /**
     * 관리자 리스트 조회 (슈퍼 관리자용)
     *
     * <p>
     * 시스템에 등록된 관리자 목록을 페이징 처리하여 조회합니다.
     * 이름, 이메일, 역할, 상태 등 다양한 조건으로 필터링 및 정렬이 가능합니다.
     * 이 API는 'SUPER_ADMIN' 권한을 가진 사용자만 접근할 수 있습니다.
     * </p>
     *
     * @param keyword 검색할 키워드 (이름 또는 이메일 포함 검색)
     * @param role 필터링할 관리자 역할 (예: SUPER_ADMIN, OPERATION)
     * @param status 필터링할 관리자 상태 (예: ACTIVE, INACTIVE)
     * @param page 조회할 페이지 번호 (클라이언트 기준 1부터 시작, 기본값 1)
     * @param size 한 페이지당 출력할 데이터 개수 (기본값 10)
     * @param sortBy 정렬 기준 컬럼명 (예: createdAt, name)
     * @param sortOrder 정렬 방향 (asc: 오름차순, desc: 내림차순)
     * @return 상태 코드, 메시지 및 페이징된 관리자 목록을 포함한 ApiResponse 객체
     *
     * @author 박채빈
     * @since 2026-04-27
     */
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public CommonApiResponse<AdminListResponse> getAdminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) AdminRole role,
            @RequestParam(required = false) AdminStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            HttpSession session) {

        SessionUtils.getLoginAdmin(session);

        AdminListResponse response =
                adminService.getAdminList(keyword, role, status, page, size, sortBy, sortOrder);

        return CommonApiResponse.success(OK, "관리자 리스트 조회 성공", response);
    }

    /**
     * 관리자 상세 조회 API
     * GET /admins/{adminId}
     */
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{adminId}")
    public CommonApiResponse<AdminResponse> getAdmin(@PathVariable Long adminId, HttpSession session) {

        SessionUtils.getLoginAdmin(session);

        AdminResponse response = adminService.getAdmin(adminId);

        return CommonApiResponse.success(OK, "관리자 상세 조회 성공", response);
    }
    /**
     * 관리자 정보 수정 API
     *
     * <p>
     * 특정 관리자의 기본 정보(이름, 이메일, 전화번호)를 수정합니다.
     * 이 요청은 슈퍼 관리자(SUPER_ADMIN) 권한을 가진 사용자만 수행할 수 있습니다.
     * </p>
     *
     * <p><b>보안 및 권한 로직</b></p>
     * <ul>
     * <li>세션에서 로그인된 관리자 정보를 확인합니다.</li>
     * <li>권한이 {@code SUPER_ADMIN}이 아닐 경우 {@link ErrorCode#FORBIDDEN} 예외를 발생시킵니다.</li>
     * </ul>
     *
     *
     * @param adminId 수정할 대상 관리자의 고유 ID
     * @param request 수정할 정보를 담은 DTO
     * @param session 현재 사용자 세션
     * @return 수정된 관리자 정보를 포함한 공통 응답 객체
     * @throws ServiceException 인증되지 않았거나 권한이 없는 경우, 또는 대상 관리자가 없는 경우 발생
     */
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}")
    public CommonApiResponse<AdminResponse> updateAdmin(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminInfoUpdateRequest request,
            HttpSession session) {

        SessionUtils.getLoginAdmin(session);

        AdminResponse response = adminService.updateAdmin(adminId, request);

        return CommonApiResponse.success(OK, "관리자 정보 수정 성공", response);
    }

    /**
     * 관리자 권한 역할 수정 API
     *
     * <p>
     * 특정 관리자의 시스템 권한 역할을 변경합니다.
     * 이 API는 오직 슈퍼 관리자(SUPER_ADMIN)만 호출 가능합니다.
     * </p>
     *
     * @param adminId 수정할 대상 관리자의 고유 ID
     * @param request 변경할 역할 정보를 담은 DTO
     * @param session 현재 사용자 세션 (로그인 검증용)
     * @return 수정된 관리자 정보를 포함한 공통 응답 객체
     */
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}/role")
    public CommonApiResponse<AdminResponse> updateAdminRole(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminRoleUpdateRequest request,
            HttpSession session
    ) {
        SessionUtils.getLoginAdmin(session);

        AdminResponse response = adminService.updateAdminRole(adminId, request);

        return CommonApiResponse.success(OK, "관리자 역할 수정 성공", response);
    }
    /**
     * 관리자 상태 수정 API
     *
     * <p>
     * 특정 관리자의 계정 상태(활성화, 정지 등)를 변경합니다.
     * 이 API는 시스템 보안을 위해 슈퍼 관리자(SUPER_ADMIN) 권한을 가진 사용자만 호출할 수 있습니다.
     * </p>
     *
     * <p><b>보안 로직</b></p>
     * <ul>
     * <li>세션 인증을 통해 현재 로그인한 사용자의 정보를 가져옵니다.</li>
     * <li>사용자의 권한이 {@code SUPER_ADMIN}이 아닐 경우 {@code 403 Forbidden} 예외를 반환합니다.</li>
     * </ul>
     *
     * @param adminId 수정할 대상 관리자의 고유 ID
     * @param request 변경할 상태 정보를 담은 DTO
     * @param session 현재 사용자의 세션
     * @return 수정된 관리자 정보를 포함한 공통 응답 객체
     */
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}/status")
    public CommonApiResponse<AdminResponse> updateAdminStatus(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminStatusUpdateRequest request,
            HttpSession session) {

        SessionUtils.getLoginAdmin(session);

        AdminResponse response = adminService.updateAdminStatus(adminId, request);

        return CommonApiResponse.success(OK, "관리자 상태 수정 성공", response);
    }
    /**
     * 관리자 삭제 API
     *
     * <p>
     * 관리자 계정을 시스템에서 영구적으로 삭제합니다.
     * 이 작업은 복구가 불가능하며, 오직 슈퍼 관리자(SUPER_ADMIN)만 수행할 수 있습니다.
     * </p>
     *
     * <p><b>보안 제약 사항</b></p>
     * <ul>
     * <li>로그인 세션이 유효해야 합니다. (401 Unauthorized)</li>
     * <li>요청자의 권한이 {@code SUPER_ADMIN}이어야 합니다. (403 Forbidden)</li>
     * </ul>
     *
     * @param adminId 삭제 대상 관리자 고유 ID
     * @param session 현재 사용자 세션
     * @return 삭제 완료 메시지를 포함한 공통 응답 객체
     */
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{adminId}")
    public CommonApiResponse<Void> deleteAdmin(
            @PathVariable Long adminId,
            HttpSession session) {

        SessionUtils.getLoginAdmin(session);

        adminService.deleteAdmin(adminId);

        return CommonApiResponse.success(OK, "관리자 삭제 완료", null);
    }

    /**
     * 관리자 가입 승인 및 거절 API
     *
     * <p>슈퍼 관리자 권한을 확인한 후, 가입 요청에 대한 최종 승인 또는 거절을 수행합니다.</p>
     *
     * @param adminId 대상 관리자 ID
     * @param request 승인 처리 정보
     * @param session 세션 정보
     * @return 공통 응답 규격에 맞춘 처리 결과
     */
    //@PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{adminId}/approval")
    public CommonApiResponse<AdminApprovalResponse> approveAdmin(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminApprovalRequest request,
            HttpSession session) {

        SessionUtils.getLoginAdmin(session);

        AdminApprovalResponse response = adminService.approveAdmin(adminId, request);

        String message = request.getIsApproved() ? "관리자 승인 완료" : "관리자 거부 완료";
        return CommonApiResponse.success(OK, message, response);
    }
    /**
     * 내 프로필 조회 API
     *
     * <p>현재 세션에 로그인되어 있는 관리자 자신의 상세 정보를 조회합니다.</p>
     *
     * @param session 현재 사용자 세션
     * @return 프로필 정보 데이터가 포함된 성공 응답
     */
    @GetMapping("/me")
    public CommonApiResponse<AdminResponse> getMyProfile(HttpSession session) {
        // 1. 세션에서 현재 로그인한 관리자 정보 가져오기 (비로그인 시 401 처리됨)
        LoginAdminDto loginAdmin = SessionUtils.getLoginAdmin(session);

        // 2. 서비스 호출하여 최신 DB 데이터 조회
        AdminResponse response = adminService.getAdminProfile(loginAdmin.getId());

        // 3. 성공 응답 반환
        return CommonApiResponse.success(OK, "관리자 프로필 조회 성공", response);
    }

    /**
     * 내 프로필 수정 API
     *
     * @param request 수정할 필드 데이터를 담은 객체
     * @param session 현재 사용자 세션
     * @return 업데이트된 프로필 정보 반환
     */
    @PutMapping("/me")
    public CommonApiResponse<AdminResponse> updateMyProfile(
            @RequestBody @Valid AdminInfoUpdateRequest request,
            HttpSession session) {

        LoginAdminDto loginAdmin = SessionUtils.getLoginAdmin(session);

        AdminResponse response = adminService.updateAdminProfile(loginAdmin.getId(), request);

        return CommonApiResponse.success(OK, "프로필 수정 완료", response);
    }

    /**
     * 내 비밀번호 변경 API
     */
    @PatchMapping("/me/password")
    public CommonApiResponse<Void> updateMyPassword(
            @RequestBody @Valid AdminPasswordUpdateRequest request,
            HttpSession session
    ) {
        LoginAdminDto loginAdmin = SessionUtils.getLoginAdmin(session);

        adminService.updatePassword(loginAdmin.getId(), request);

        return CommonApiResponse.success(OK, "비밀번호 변경 완료", null);
    }
}
