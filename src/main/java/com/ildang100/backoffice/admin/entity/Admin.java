package com.ildang100.backoffice.admin.entity;

import com.ildang100.backoffice.admin.dto.AdminInfoUpdateRequest;
import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import com.ildang100.backoffice.config.PasswordEncoder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 관리자 정보를 나타내는 엔티티입니다.
 *
 * <p>
 * 관리자 계정의 기본 정보와 권한, 상태를 관리하며,
 * JPA를 통해 DB의 admins 테이블과 매핑됩니다.
 * </p>
 *
 * <p>
 * 생성은 팩토리 메서드(create)를 통해서만 가능하며,
 * 생성 시 기본 상태는 PENDING_APPROVAL(승인 대기)로 설정됩니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-26
 */
@Entity
@Getter
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String tele;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AdminRole role;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AdminStatus status;

    private LocalDateTime approvedAt;

    private Admin(String name, String email, String password, String tele, AdminRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.tele = tele;
        this.role = role;
        this.status = AdminStatus.PENDING_APPROVAL;
        this.approvedAt = null;
    }

    /**
     * 관리자 엔티티 생성 팩토리 메서드
     *
     * <p>
     * 회원가입 시 호출되며, 생성 시 관리자 상태는 자동으로 PENDING_APPROVAL로 설정됩니다.
     * </p>
     *
     * @param name     관리자 이름
     * @param email    관리자 이메일
     * @param password 관리자 비밀번호
     * @param tele     관리자 전화번호
     * @param role     관리자 권한
     * @return 생성된 Admin 엔티티
     */
    public static Admin create(String name, String email, String password, String tele, AdminRole role) {
        return new Admin(name, email, password, tele, role);
    }

    /**
     * 관리자 기본 정보 수정
     *
     * <p>
     * 관리자의 이름, 이메일, 전화번호 정보를 일괄 수정합니다.
     * </p>
     *
     * <p><b>비즈니스 로직</b></p>
     * <ul>
     * <li>전달된 파라미터가 null이거나 공백이 아닐 경우에만 필드를 업데이트합니다.</li>
     * <li>JPA의 변경 감지(Dirty Checking)를 통해 트랜잭션 종료 시점에 반영됩니다.</li>
     * </ul>
     *
     */
    public void update(AdminInfoUpdateRequest request) {
        if (StringUtils.hasText(request.getName())) {
            this.name = request.getName();
        }
        if (StringUtils.hasText(request.getEmail())) {
            this.email = request.getEmail();
        }
        if (StringUtils.hasText(request.getTele())) {
            this.tele = request.getTele();
        }
    }

    /**
     * 관리자 권한 역할 수정
     *
     * <p>
     * 관리자의 시스템 접근 권한(Role)을 변경합니다.
     * </p>
     *
     * @param role 변경할 관리자 역할 (SUPER_ADMIN, OPERATIONS_ADMIN, CS_ADMIN 등)
     */
    public void updateRole(AdminRole role) {
        this.role = role;
    }

    /**
     * 관리자 상태 수정
     *
     * <p>
     * 관리자의 계정 활동 상태를 변경합니다.
     * </p>
     *
     * @param status 변경할 관리자 상태 (ACTIVE, INACTIVE, SUSPENDED 등)
     */
    public void updateStatus(AdminStatus status) {
        this.status = status;
    }

    /**
     * 관리자 승인 처리
     * @param approvedAt 승인 일시
     */
    public void approve(LocalDateTime approvedAt) {
        validatePendingStatus();
        this.status = AdminStatus.ACTIVE;
        this.approvedAt = approvedAt;
    }

    /**
     * 관리자 거절 처리
     */
    public void reject() {
        validatePendingStatus();
        this.status = AdminStatus.REJECTED;
    }

    /**
     * 상태 변경 가능 여부 검증 (내부 캡슐화)
     */
    private void validatePendingStatus() {
        if (this.status != AdminStatus.PENDING_APPROVAL) {
            throw new ServiceException(ErrorCode.ALREADY_PROCESSED_ADMIN);
        }
    }

    /**
     * 관리자 계정의 로그인 가능 여부를 검증합니다.
     *
     * <p>
     * 로그인 시도 시 계정 상태에 따라 접근 가능 여부를 판단하기 위해 사용됩니다.
     * 실제 상태별 검증 로직은 {@link AdminStatus#validateLoginable()}에 위임합니다.
     * </p>
     *
     * <p>
     * 예를 들어 다음과 같은 상태에서는 로그인이 제한됩니다:
     * <ul>
     *     <li>PENDING_APPROVAL - 승인 대기</li>
     *     <li>REJECTED - 승인 거절</li>
     *     <li>SUSPENDED - 계정 정지</li>
     *     <li>INACTIVE - 비활성 계정</li>
     * </ul>
     * </p>
     *
     * @throws ServiceException 로그인할 수 없는 계정 상태인 경우
     */
    public void validateLoginAvailable() {
        this.status.validateLoginable();
    }

    /**
     * 비밀번호 변경 (객체지향적 설계)
     * <p>엔티티 스스로 현재 비밀번호를 검증하고, 통과 시 새 비밀번호를 암호화하여 업데이트합니다.</p>
     *
     * @param currentPassword 입력받은 현재 비밀번호 (평문)
     * @param newPassword     변경할 새 비밀번호 (평문)
     * @param passwordEncoder 암호화 모듈
     */
    public void changePassword(String currentPassword, String newPassword, PasswordEncoder passwordEncoder) {

        if (!passwordEncoder.matches(currentPassword, this.password)) {
            throw new ServiceException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        this.password = passwordEncoder.encode(newPassword);
    }
}