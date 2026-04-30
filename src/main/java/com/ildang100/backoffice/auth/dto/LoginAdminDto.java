package com.ildang100.backoffice.auth.dto;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.enums.AdminRole;
import lombok.Getter;

/**
 * 인증된 관리자 정보를 담는 DTO입니다.
 *
 * <p>
 * JWT 인증이 성공하면 {@code JwtAuthenticationFilter}가 이 객체를 만들어
 * Spring SecurityContext의 principal로 저장합니다.
 * 컨트롤러나 서비스 진입 전 현재 로그인한 관리자 ID, 이메일, 역할을 확인할 때 사용합니다.
 * </p>
 */
@Getter
public class LoginAdminDto {

    private final Long id;
    private final String email;
    private final AdminRole role;

    private LoginAdminDto(Long id, String email, AdminRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    /**
     * DB에서 조회한 관리자 엔티티를 인증 principal로 사용할 DTO로 변환합니다.
     */
    public static LoginAdminDto from(Admin admin) {
        return new LoginAdminDto(
                admin.getId(),
                admin.getEmail(),
                admin.getRole()
        );
    }
}
