package com.ildang100.backoffice.auth.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 관련 설정 값을 바인딩하는 프로퍼티 클래스입니다.
 *
 * <p>
 * application.yml 또는 application.properties에 정의된
 * JWT 설정 값을 주입받아 토큰 생성 및 검증에 사용합니다.
 * </p>
 *
 * <p><b>주요 설정 값</b></p>
 * <ul>
 *     <li>secret: JWT 서명(Signature)에 사용되는 비밀 키</li>
 *     <li>accessTokenValidTime: Access Token의 유효 기간 (밀리초 단위)</li>
 * </ul>
 *
 * <p>
 * 해당 값들은 {@link JwtProvider}에서 토큰 생성 및 검증 시 활용되며,
 * 보안과 직결되므로 외부 설정 파일에서 관리됩니다.
 * </p>
 *
 * <p>
 * 특히 secret 값은 충분히 긴 랜덤 문자열을 사용해야 하며,
 * 외부에 노출되지 않도록 주의해야 합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
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
