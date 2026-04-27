package com.ildang100.backoffice.admin.service;

import com.ildang100.backoffice.admin.dto.AdminListResponse;
import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminManageService {

    private final AdminRepository adminRepository;

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
}
