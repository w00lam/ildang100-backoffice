package com.ildang100.backoffice.admin.repository;

import com.ildang100.backoffice.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 관리자(Admin) 엔티티에 대한 데이터 접근을 담당하는 Repository 인터페이스입니다.
 *
 * <p>
 * {@link JpaRepository}를 상속받아 기본적인 CRUD 기능을 제공하며,
 * 관리자 도메인에 특화된 조회 메서드를 정의합니다.
 * </p>
 *
 * <p><b>주요 기능</b></p>
 * <ul>
 *     <li>관리자 정보 저장 및 조회</li>
 *     <li>이메일 중복 여부 확인</li>
 * </ul>
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 이메일 중복 여부 확인
     *
     * <p>
     * 회원가입 시 동일한 이메일이 이미 존재하는지 확인하기 위해 사용됩니다.
     * </p>
     *
     * @param email 확인할 이메일
     * @return 중복이면 {@code true}, 아니면 {@code false}
     *
     * @author 이우람
     * @since 2026-04-27
     */
    boolean existsByEmail(String email);

    /**
     * 이메일로 관리자 조회
     *
     * <p>
     * 로그인 시 사용되며, 이메일을 기준으로 관리자를 조회합니다.
     * 존재하지 않을 경우 Optional.empty()를 반환합니다.
     * </p>
     *
     * @param email 관리자 이메일
     * @return 관리자 Optional
     *
     * @author 이우람
     * @since 2026-04-27
     */
    Optional<Admin> findByEmail(String email);
}
