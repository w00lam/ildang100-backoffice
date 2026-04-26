package com.ildang100.backoffice.auth.dto;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.enums.AdminRole;
import lombok.Getter;

/**
 * 로그인된 관리자의 최소 정보를 담는 DTO입니다.
 *
 * <p>
 * 세션에 저장되어 인증 이후 요청에서 사용자 식별 및 권한 확인에 사용됩니다.
 * 비밀번호 등 민감한 정보는 포함하지 않으며, 인증에 필요한 최소 정보만 보유합니다.
 * </p>
 *
 * <p>
 * 해당 객체는 불변(immutable) 객체로 설계되어 세션에 저장 시 안전성을 보장합니다.
 * 생성은 외부에서 직접 하지 않고, 팩토리 메서드를 통해서만 생성됩니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-26
 */
@Getter
public class LoginAdminDto {

    private final Long id;
    private final String email;
    private final String name;
    private final AdminRole role;

    /**
     * 외부에서 직접 생성하지 못하도록 제한된 생성자
     *
     * <p>
     * 객체 생성은 반드시 팩토리 메서드를 통해 이루어지도록 하여
     * 생성 로직을 통제하고 일관성을 유지합니다.
     * </p>
     */
    private LoginAdminDto(Long id, String email, String name, AdminRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    /**
     * Admin 엔티티를 LoginAdminDto로 변환하는 팩토리 메서드입니다.
     *
     * <p>
     * DB에서 조회된 관리자 엔티티를 세션에 저장 가능한 DTO 형태로 변환합니다.
     * 인증 이후 요청에서 사용할 최소 정보만 추출하여 전달합니다.
     * </p>
     *
     * @param admin 관리자 엔티티
     * @return LoginAdminDto 세션에 저장될 관리자 정보 DTO
     */
    public static LoginAdminDto from(Admin admin) {
        return new LoginAdminDto(
                admin.getId(),
                admin.getEmail(),
                admin.getName(),
                admin.getRole()
        );
    }
}