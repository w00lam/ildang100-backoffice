package com.ildang100.backoffice.admin.dto;

import com.ildang100.backoffice.admin.entity.Admin;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 관리자 목록 조회 응답 DTO입니다.
 *
 * <p>
 * 관리자 목록과 페이지네이션 메타 정보를 함께 제공합니다.
 * {@code page}는 클라이언트 요청 기준인 1부터 시작하는 페이지 번호입니다.
 * </p>
 *
 * @author 박채빈
 * @since 2026-04-27
 */
@Getter
public class AdminListResponse {

    private final List<AdminResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    private AdminListResponse(
            List<AdminResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages)
    {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    /**
     * 관리자 엔티티 페이지를 관리자 목록 응답 DTO로 변환합니다.
     *
     * <p>
     * 내부적으로 AdminResponse의 from 메서드를 호출하여
     * 개별 엔티티들을 DTO 리스트로 변환합니다.
     * </p>
     *
     * @param admins 관리자 엔티티 페이지
     * @return 관리자 목록 응답 DTO
     *
     * @author 박채빈
     * @since 2026-04-27
     */
    public static AdminListResponse from(Page<Admin> admins) {
        // 엔티티 리스트를 DTO 리스트로 변환
        List<AdminResponse> content = admins.getContent().stream()
                .map(AdminResponse::from)
                .toList();

        return new AdminListResponse(
                content,
                admins.getNumber() + 1, // JPA의 0-based를 1-based로 변환
                admins.getSize(),
                admins.getTotalElements(),
                admins.getTotalPages()
        );
    }
}