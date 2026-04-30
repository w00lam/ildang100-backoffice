package com.ildang100.backoffice.auth.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 값을 관리합니다.
 *
 * <p>
 * application 설정 파일에 정의된 secret과 access token 만료 시간을 주입받아
 * {@link JwtProvider}에서 토큰 생성과 검증에 사용합니다.
 * </p>
 *
 * <p>
 * secret은 토큰 서명에 사용되므로 외부에 노출되면 안 됩니다.
 * 실제 배포 환경에서는 환경변수나 별도 secret 관리 도구로 관리하는 것이 좋습니다.
 * </p>
 */
@Getter
@Component
public class JwtProperties {

    private final String secret;
    private final long accessTokenValidTime;

    public JwtProperties(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-valid-time}") long accessTokenValidTime
    ) {
        this.secret = secret;
        this.accessTokenValidTime = accessTokenValidTime;
    }
}
