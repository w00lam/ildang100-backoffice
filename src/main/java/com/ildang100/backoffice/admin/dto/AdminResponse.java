package com.ildang100.backoffice.admin.dto;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class AdminResponse {

    // 1. 관리자 데이터 응답 DTO
    private final Long adminId;
    private final String name;
    private final String email;
    private final String tele;
    private final AdminRole role;
    private final AdminStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime approvedAt;

    private AdminResponse(Long adminId, String name, String email, String tele, AdminRole role,
                          AdminStatus status, LocalDateTime createdAt, LocalDateTime approvedAt) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.tele = tele;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
    }

    // Entity -> DTO 변환을 DTO 내부에서 처리
    public static AdminResponse from(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getTele(),
                admin.getRole(),
                admin.getStatus(),
                admin.getCreatedAt(),
                admin.getApprovedAt()
        );
    }
}
