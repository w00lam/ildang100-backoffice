package com.ildang100.backoffice.admin.controller;

import com.ildang100.backoffice.admin.dto.AdminListResponse;
import com.ildang100.backoffice.admin.service.AdminManageService;
import com.ildang100.backoffice.auth.util.SessionUtils;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
                adminManageService.getAdminList(keyword, role, status, page, size, sortBy, sortOrder);

        return CommonApiResponse.success(OK, "관리자 리스트 조회 성공", response);
    }
}
