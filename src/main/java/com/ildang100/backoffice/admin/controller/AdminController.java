package com.ildang100.backoffice.admin.controller;

import com.ildang100.backoffice.admin.dto.*;
import com.ildang100.backoffice.admin.service.AdminService;
import com.ildang100.backoffice.auth.dto.LoginAdminDto;
import com.ildang100.backoffice.auth.util.AuthUtils;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리자 목록을 조회합니다.
     *
     * <p>SUPER_ADMIN 권한을 가진 관리자만 접근할 수 있습니다.</p>
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public CommonApiResponse<AdminListResponse> getAdminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) AdminRole role,
            @RequestParam(required = false) AdminStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ) {
        AdminListResponse response =
                adminService.getAdminList(keyword, role, status, page, size, sortBy, sortOrder);

        return CommonApiResponse.success(OK, "관리자 리스트 조회 성공", response);
    }

    /**
     * 특정 관리자 상세 정보를 조회합니다.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{adminId}")
    public CommonApiResponse<AdminResponse> getAdmin(@PathVariable Long adminId) {
        AdminResponse response = adminService.getAdmin(adminId);

        return CommonApiResponse.success(OK, "관리자 상세 조회 성공", response);
    }

    /**
     * 특정 관리자 기본 정보를 수정합니다.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}")
    public CommonApiResponse<AdminResponse> updateAdmin(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminInfoUpdateRequest request
    ) {
        AdminResponse response = adminService.updateAdmin(adminId, request);

        return CommonApiResponse.success(OK, "관리자 정보 수정 성공", response);
    }

    /**
     * 특정 관리자의 역할을 수정합니다.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}/role")
    public CommonApiResponse<AdminResponse> updateAdminRole(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminRoleUpdateRequest request
    ) {
        AdminResponse response = adminService.updateAdminRole(adminId, request);

        return CommonApiResponse.success(OK, "관리자 역할 수정 성공", response);
    }

    /**
     * 특정 관리자의 계정 상태를 수정합니다.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}/status")
    public CommonApiResponse<AdminResponse> updateAdminStatus(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminStatusUpdateRequest request
    ) {
        AdminResponse response = adminService.updateAdminStatus(adminId, request);

        return CommonApiResponse.success(OK, "관리자 상태 수정 성공", response);
    }

    /**
     * 특정 관리자 계정을 삭제합니다.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{adminId}")
    public CommonApiResponse<Void> deleteAdmin(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);

        return CommonApiResponse.success(OK, "관리자 삭제 완료", null);
    }

    /**
     * 관리자 가입 요청을 승인하거나 거절합니다.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{adminId}/approval")
    public CommonApiResponse<AdminApprovalResponse> approveAdmin(
            @PathVariable Long adminId,
            @RequestBody @Valid AdminApprovalRequest request
    ) {
        AdminApprovalResponse response = adminService.approveAdmin(adminId, request);

        String message = request.getIsApproved() ? "관리자 승인 완료" : "관리자 거절 완료";

        return CommonApiResponse.success(OK, message, response);
    }

    /**
     * 현재 로그인한 관리자의 프로필을 조회합니다.
     *
     * <p>로그인 관리자 정보는 JWT 인증 후 SecurityContext에 저장된 값을 사용합니다.</p>
     */
    @GetMapping("/me")
    public CommonApiResponse<AdminResponse> getMyProfile() {
        LoginAdminDto loginAdmin = AuthUtils.getLoginAdmin();

        AdminResponse response = adminService.getAdminProfile(loginAdmin.getId());

        return CommonApiResponse.success(OK, "관리자 프로필 조회 성공", response);
    }

    /**
     * 현재 로그인한 관리자의 프로필을 수정합니다.
     *
     * <p>로그인 관리자 정보는 JWT 인증 후 SecurityContext에 저장된 값을 사용합니다.</p>
     */
    @PutMapping("/me")
    public CommonApiResponse<AdminResponse> updateMyProfile(
            @RequestBody @Valid AdminInfoUpdateRequest request
    ) {
        LoginAdminDto loginAdmin = AuthUtils.getLoginAdmin();

        AdminResponse response = adminService.updateAdminProfile(loginAdmin.getId(), request);

        return CommonApiResponse.success(OK, "프로필 수정 완료", response);
    }

    /**
     * 현재 로그인한 관리자의 비밀번호를 변경합니다.
     */
    @PatchMapping("/me/password")
    public CommonApiResponse<Void> updateMyPassword(
            @RequestBody @Valid AdminPasswordUpdateRequest request
    ) {
        LoginAdminDto loginAdmin = AuthUtils.getLoginAdmin();

        adminService.updatePassword(loginAdmin.getId(), request);

        return CommonApiResponse.success(OK, "비밀번호 변경 완료", null);
    }
}
