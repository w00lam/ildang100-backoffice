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
 * JWT 토큰 생성 및 검증을 담당하는 클래스입니다.
 *
 * <p>
 * 관리자 로그인 시 Access Token을 생성하고,
 * 이후 요청에서 토큰을 검증하여 사용자 정보를 추출합니다.
 * </p>
 *
 * <p><b>주요 기능</b></p>
 * <ul>
 *     <li>Access Token 생성</li>
 *     <li>토큰에서 사용자 ID 추출</li>
 *     <li>토큰 서명 및 만료 시간 검증</li>
 * </ul>
 *
 * <p>
 * 토큰에는 사용자 ID(subject), 이메일, 권한(role) 정보가 포함됩니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

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

    public Long getAdminId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
