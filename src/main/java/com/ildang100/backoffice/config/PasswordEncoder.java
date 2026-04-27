package com.ildang100.backoffice.config;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * 비밀번호 암호화 및 검증을 담당하는 컴포넌트입니다.
 *
 * <p>
 * BCrypt 알고리즘을 사용하여 비밀번호를 안전하게 해싱하고,
 * 로그인 시 입력된 비밀번호와 저장된 해시값을 비교하는 기능을 제공합니다.
 * </p>
 *
 * <p><b>주요 기능</b></p>
 * <ul>
 *     <li>평문 비밀번호를 BCrypt 해시로 암호화</li>
 *     <li>입력 비밀번호와 저장된 해시값 비교</li>
 * </ul>
 *
 * <p>
 * BCrypt는 단방향 해시 함수로, 원본 비밀번호를 복원할 수 없으며
 * 내부적으로 salt를 포함하여 보안성을 높입니다.
 * </p>
 *
 * @author sparta
 * @since 2026-04-27
 */
@Component
public class PasswordEncoder {

    public String encode(String rawPassword) {
        return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
        return result.verified;
    }
}
