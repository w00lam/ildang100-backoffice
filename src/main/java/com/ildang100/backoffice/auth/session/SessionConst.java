package com.ildang100.backoffice.auth.session;

/**
 * 세션에서 사용하는 key를 정의하는 상수 클래스입니다.
 *
 * <p>
 * 세션에 저장/조회 시 사용하는 문자열 key를 중앙에서 관리하여
 * 오타 및 불일치로 인한 버그를 방지합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-26
 */
public final class SessionConst {

    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자
     */
    private SessionConst() {
    }

    /**
     * 로그인한 관리자 정보를 저장하는 세션 key
     */
    public static final String LOGIN_ADMIN = "loginAdmin";
}