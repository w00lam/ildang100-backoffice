package com.ildang100.backoffice.admin.repository;

import com.ildang100.backoffice.admin.entity.Admin;
import com.ildang100.backoffice.common.enums.AdminRole;
import com.ildang100.backoffice.common.enums.AdminStatus;
import com.ildang100.backoffice.common.exception.ErrorCode;
import com.ildang100.backoffice.common.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
     * 이메일 중복 여부 확인(본인 제외)
     *
     * <p>
     * 정보변경 시 본인의 이메일을 제외한 동일한 이메일이 이미 존재하는지 확인하기 위해 사용됩니다.
     * </p>
     *
     * @param email 확인할 이메일
     * @param id 확인 제외할 관리자 아이디
     * @return 중복이면 {@code true}, 아니면 {@code false}
     *
     * @author 박채빈
     * @since 2026-04-28
     */
    boolean existsByEmailAndIdNot(String email, Long id);

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

    /**
     * 관리자 목록 동적 검색 및 페이징 조회
     *
     * <p>
     * 키워드(이름 또는 이메일), 권한(Role), 상태(Status) 조건을 기반으로 관리자 목록을 검색합니다.
     * 전달된 파라미터가 {@code null}일 경우 해당 검색 조건은 무시되며, 값이 존재하는 파라미터들만 AND 조건으로 결합되어 조회됩니다.
     * 키워드는 부분 일치(LIKE) 검색을 수행하며, 역할과 상태는 정확히 일치하는 데이터를 찾습니다.
     * </p>
     *
     * @param keyword 검색할 이름 또는 이메일의 부분 키워드 (null 허용)
     * @param role 필터링할 관리자의 특정 권한 Enum (null 허용)
     * @param status 필터링할 관리자의 특정 상태 Enum (null 허용)
     * @param pageable 페이징 및 정렬 요청 정보
     * @return 조건에 부합하는 관리자 엔티티들이 담긴 {@link Page} 객체
     *
     * @author 박채빈
     * @since 2026-04-27
     */
    @Query("SELECT a FROM Admin a " +
            "WHERE (:keyword IS NULL OR a.name LIKE %:keyword% OR a.email LIKE %:keyword%) " +
            "AND (:role IS NULL OR a.role = :role) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Admin> findAdminsByCondition(
            @Param("keyword") String keyword,
            @Param("role") AdminRole role,
            @Param("status") AdminStatus status,
            Pageable pageable
    );

    /**
     * 관리자 상태별 개수를 조회합니다.
     *
     * <p>
     * 대시보드 Summary 통계에서 활성 관리자 수를 계산하기 위해 사용됩니다.
     * </p>
     *
     * @param status 관리자 상태
     * @return 해당 상태의 관리자 수
     *
     * @author 이우람
     * @since 2026-04-27
     */
    long countByStatus(AdminStatus status);

    /**
     * 예외 처리까지 완료된 엔티티를 반환하는 공통 메서드
     * @author 박채빈
     * @since 2026-04-29
     */
    default Admin getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.ADMIN_NOT_FOUND));
    }
}
