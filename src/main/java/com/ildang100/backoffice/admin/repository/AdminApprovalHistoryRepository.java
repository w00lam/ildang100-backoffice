package com.ildang100.backoffice.admin.repository;

import com.ildang100.backoffice.admin.entity.AdminApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 관리자 승인/거절 이력 데이터 접근 레포지토리
 *
 * @author 박채빈
 * @since 2026-04-28
 */
@Repository
public interface AdminApprovalHistoryRepository extends JpaRepository<AdminApprovalHistory, Long> {
}