package com.ildang100.backoffice.admin.service;

import com.ildang100.backoffice.admin.dto.AdminListResponse;
import com.ildang100.backoffice.admin.dto.AdminResponse;
import com.ildang100.backoffice.admin.dto.AdminUpdateRequest;
import com.ildang100.backoffice.admin.entity.Admin;
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
    public AdminResponse updateAdmin(Long adminId, AdminUpdateRequest request) {
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


}
