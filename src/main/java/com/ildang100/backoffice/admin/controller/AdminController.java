package com.ildang100.backoffice.admin.controller;

import com.ildang100.backoffice.admin.dto.AdminListResponse;
import com.ildang100.backoffice.admin.dto.AdminResponse;
import com.ildang100.backoffice.admin.dto.AdminUpdateRequest;
import com.ildang100.backoffice.admin.service.AdminManageService;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminManageService adminManageService;
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
                adminManageService.getAdminList(keyword, role, status, page, size, sortBy, sortOrder);

        return CommonApiResponse.success(OK, "관리자 리스트 조회 성공", response);
    }

    /**
     * 관리자 상세 조회 API
     * GET /admins/{adminId}
     */
    @GetMapping("/{adminId}")
    public CommonApiResponse<AdminResponse> getAdmin(@PathVariable Long adminId, HttpSession session) {

        SessionUtils.getLoginAdmin(session);

        AdminResponse response = adminManageService.getAdmin(adminId);

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
     * <p><b>처리 흐름</b></p>
     * <ol>
     * <li>세션 기반 인증 및 슈퍼 관리자 권한 검증</li>
     * <li>요청 데이터(@Valid) 유효성 검사</li>
     * <li>서비스 레이어 호출을 통한 정보 수정 및 중복 검증</li>
     * <li>수정 완료된 데이터 반환</li>
     * </ol>
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
            @RequestBody @Valid AdminUpdateRequest request,
            HttpSession session
    ) {
        // 1. 로그인 여부 및 권한 체크
        LoginAdminDto loginAdmin = SessionUtils.getLoginAdmin(session);

        if (loginAdmin.getRole() != AdminRole.SUPER_ADMIN) {
            throw new ServiceException(ErrorCode.FORBIDDEN);
        }

        AdminResponse response = adminManageService.updateAdmin(adminId, request);

        return CommonApiResponse.success(HttpStatus.OK, "관리자 정보 수정 성공", response);
    }
}
