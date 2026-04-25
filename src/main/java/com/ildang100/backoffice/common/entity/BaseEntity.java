package com.ildang100.backoffice.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * <p>공통 엔티티(BaseEntity) 클래스입니다.</p>
 *
 * <p>
 * 모든 엔티티에서 공통적으로 사용하는 생성일자와 수정일자를 관리하기 위한
 * 추상 클래스입니다. JPA Auditing 기능을 활용하여 엔티티의 생성 및 수정 시점을
 * 자동으로 저장합니다.
 * </p>
 *
 * <p><b>주요 기능</b></p>
 * <ul>
 *     <li>엔티티 생성 시 {@code createdDate} 자동 저장</li>
 *     <li>엔티티 수정 시 {@code lastModifiedDate} 자동 갱신</li>
 * </ul>
 *
 * <p><b>사용 방법</b></p>
 * <pre>
 * {@code
 * @Entity
 * public class ExampleEntity extends BaseEntity {
 *     // 필드 정의
 * }
 * }
 * </pre>
 *
 * <p><b>주의 사항</b></p>
 * <ul>
 *     <li>JPA Auditing 기능을 사용하기 위해 {@code @EnableJpaAuditing} 설정이 필요합니다.</li>
 * </ul>
 *
 * @author 이우람
 * @since 2026-04-25
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
