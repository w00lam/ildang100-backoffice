package com.ildang100.backoffice.admin.entity;

import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
     * @param name  수정할 이름
     * @param email 수정할 이메일
     * @param tele  수정할 전화번호
     */
    public void update(String name, String email, String tele) {
        this.name = name;
        this.email = email;
        this.tele = tele;
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
}