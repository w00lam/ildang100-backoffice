package com.ildang100.backoffice.auth.dto;

import lombok.Getter;

/**
 * 관리자 로그인 응답 DTO입니다.
 *
 * <p>
 * JWT 인증 방식에서 로그인 성공 시 클라이언트에 전달되는
 * Access Token 정보를 포함합니다.
 * </p>
 *
 * <p><b>구성</b></p>
 * <ul>
 *     <li>accessToken: 인증에 사용되는 JWT 토큰</li>
 *     <li>tokenType: 토큰 타입 (Bearer 고정)</li>
 * </ul>
 *
 * <p>
 * 클라이언트는 이후 요청 시 Authorization 헤더에
 * "Bearer {accessToken}" 형식으로 토큰을 포함해야 합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Getter
public class AdminLoginResponse {

    private final String accessToken;
    private final String tokenType;

    public AdminLoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
