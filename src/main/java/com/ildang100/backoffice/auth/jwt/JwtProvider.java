package com.ildang100.backoffice.auth.jwt;

import com.ildang100.backoffice.admin.entity.Admin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Access Token 생성과 검증을 담당합니다.
 *
 * <p>
 * 로그인에 성공한 관리자 정보를 기반으로 토큰을 만들고,
 * 이후 보호 API 요청에서 전달된 토큰의 서명과 만료 시간을 검증합니다.
 * </p>
 *
 * <p>
 * 토큰 payload에는 관리자 ID(subject), 이메일(email), 역할(role),
 * 발급 시간(iat), 만료 시간(exp)이 포함됩니다.
 * </p>
 */
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 로그인 성공 시 클라이언트에게 내려줄 Access Token을 생성합니다.
     */
    public String createAccessToken(Admin admin) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + jwtProperties.getAccessTokenValidTime());

        return Jwts.builder()
                .subject(String.valueOf(admin.getId()))
                .claim("email", admin.getEmail())
                .claim("role", admin.getRole().name())
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰의 subject에 저장된 관리자 ID를 꺼냅니다.
     */
    public Long getAdminId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    /**
     * 토큰을 파싱하면서 서명과 만료 시간을 함께 검증합니다.
     *
     * <p>
     * 토큰이 만료되었거나, 서명이 다르거나, 형식이 잘못된 경우 jjwt 라이브러리 예외가 발생하고
     * {@link JwtAuthenticationFilter}에서 공통 에러 응답으로 변환합니다.
     * </p>
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
