package com.ildang100.backoffice.admin.entity;

import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.AdminStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 관리자 승인/거절 이력 엔티티
 *
 * @author 박채빈
 * @since 2026-04-28
 */
@Entity
@Getter
@Table(name = "history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminApprovalHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AdminStatus status;

    @Column(length = 100)
    private String rejectReason;

    private LocalDateTime rejectedAt;

    @Builder
    private AdminApprovalHistory(Long adminId, AdminStatus status, String rejectReason, LocalDateTime rejectedAt) {
        this.adminId = adminId;
        this.status = status;
        this.rejectReason = rejectReason;
        this.rejectedAt = rejectedAt;
    }
}