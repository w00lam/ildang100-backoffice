package com.ildang100.backoffice.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.enums.AdminStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 관리자 승인 처리 결과 응답 DTO
 * * <p>승인 시 {@code approvedAt}을, 거절 시 {@code rejectedAt}을 반환합니다.</p>
 *
 * @author 박채빈
 * @since 2026-04-28
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({ "id", "status", "rejectReason", "approvedAt", "rejectedAt", "updatedAt" })
public class AdminApprovalResponse {

    private final Long id;
    private final AdminStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String rejectReason;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDateTime approvedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDateTime rejectedAt;

    private final LocalDateTime updatedAt;

    /**
     * 처리 결과에 따라 승인 날짜 또는 거절 날짜를 포함하여 DTO를 생성합니다.
     *
     * @param admin 처리된 관리자 엔티티
     * @param rejectReason 거절 사유 (승인 시 null)
     * @param processedAt 처리 시각
     * @return AdminApprovalResponse
     */
    public static AdminApprovalResponse from(Admin admin, String rejectReason, LocalDateTime processedAt) {
        AdminApprovalResponseBuilder builder = AdminApprovalResponse.builder()
                .id(admin.getId())
                .status(admin.getStatus())
                .updatedAt(admin.getUpdatedAt());

        if (admin.getStatus() == AdminStatus.ACTIVE) {
            builder.approvedAt(processedAt);
        } else {
            builder.rejectedAt(processedAt);
            builder.rejectReason(rejectReason);
        }

        return builder.build();
    }
}