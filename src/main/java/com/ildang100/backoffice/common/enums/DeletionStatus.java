package com.ildang100.backoffice.common.enums;

/**
 * 엔티티의 소프트 삭제(soft delete) 상태를 나타내는 공통 enum입니다.
 * @author js-kim-arc
 * @since 2026-04-28
 */
public enum DeletionStatus {

    /** 삭제되지 않은 상태 (기본값). */
    NOT_DELETED,

    /** 소프트 삭제 처리된 상태. */
    DELETED
}