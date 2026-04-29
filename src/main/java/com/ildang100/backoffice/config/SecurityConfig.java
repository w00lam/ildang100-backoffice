package com.ildang100.backoffice.config;

import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.auth.jwt.JwtAuthenticationEntryPoint;
import com.ildang100.backoffice.auth.jwt.JwtAuthenticationFilter;
import com.ildang100.backoffice.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스입니다.
 *
 * <p>
 * JWT 기반 인증 방식을 사용하기 위해 기본 Security 설정을 커스터마이징합니다.
 * </p>
 *
 * <p><b>주요 설정</b></p>
 * <ul>
 *     <li>CSRF, Form Login, Http Basic 비활성화 (Stateless 환경)</li>
 *     <li>세션 미사용 설정 추가 예정(테스트 편의성) (SessionCreationPolicy.STATELESS)</li>
 *     <li>JWT 인증 필터 등록</li>
 *     <li>인증 실패 시 JwtAuthenticationEntryPoint 처리</li>
 *     <li>회원가입/로그인 API는 인증 없이 접근 허용</li>
 * </ul>
 *
 * <p>
 * 모든 요청은 JWT 토큰 기반으로 인증되며,
 * 필터에서 토큰 검증 후 SecurityContext에 인증 정보를 설정합니다.
 * </p>
 *
 * @author 이우람
 * @since 2026-04-29
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final AdminRepository adminRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admins/signup", "/admins/login").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, adminRepository),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
