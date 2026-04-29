package com.ildang100.backoffice.config;

import com.ildang100.backoffice.admin.repository.AdminRepository;
import com.ildang100.backoffice.auth.jwt.JwtAccessDeniedHandler;
import com.ildang100.backoffice.auth.jwt.JwtAuthenticationEntryPoint;
import com.ildang100.backoffice.auth.jwt.JwtAuthenticationFilter;
import com.ildang100.backoffice.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스입니다.
 *
 * <p>
 * 현재 인증 방식은 JWT 기반 Stateless 구조입니다.
 * 로그인과 회원가입을 제외한 보호 API는 Authorization 헤더의 Bearer 토큰을 검증한 뒤 접근합니다.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAccessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        // 회원가입과 로그인은 토큰 발급 전 호출해야 하므로 인증 없이 허용합니다.
                        .requestMatchers("/admins/signup", "/admins/login").permitAll()

                        // 내 정보와 로그아웃은 로그인한 관리자만 접근할 수 있습니다.
                        .requestMatchers("/admins/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/admins/logout").authenticated()

                        // 대시보드는 로그인 이후 접근하는 관리자 화면이므로 모든 관리자 역할에게 조회 권한을 허용합니다.
                        .requestMatchers(HttpMethod.GET, "/admin/dashboard/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN", "CS_ADMIN")

                        // 관리자 관리 기능은 슈퍼 관리자만 접근할 수 있습니다.
                        .requestMatchers("/admins/**").hasRole("SUPER_ADMIN")

                        // 고객 삭제는 슈퍼 관리자만 가능하고, 수정은 슈퍼/운영 관리자가 가능합니다.
                        .requestMatchers(HttpMethod.DELETE, "/admin/customers/**")
                        .hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/customers/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/admin/customers/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")

                        // 리뷰 삭제는 상품 하위 리뷰 경로를 사용하므로 실제 URL 기준으로 먼저 명시합니다.
                        .requestMatchers(HttpMethod.DELETE, "/admin/products/*/reviews/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")

                        // 상품 생성, 수정, 삭제는 슈퍼/운영 관리자가 가능합니다.
                        .requestMatchers(HttpMethod.POST, "/admin/products/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/products/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/admin/products/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/products/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")

                        // 주문 취소는 CS 관리자도 가능하지만, 일반 상태 변경은 슈퍼/운영 관리자만 가능합니다.
                        .requestMatchers(HttpMethod.PATCH, "/admin/orders/*/cancel")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN", "CS_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/admin/orders/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/orders/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")

                        // 기존 리뷰 단독 경로가 추가될 경우를 대비해 같은 삭제 권한을 유지합니다.
                        .requestMatchers(HttpMethod.DELETE, "/admin/reviews/**")
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN")

                        // 단순 조회(GET)는 모든 관리자 역할에게 허용합니다.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/customers/**",
                                "/admin/products/**",
                                "/admin/orders/**",
                                "/admin/reviews/**"
                        )
                        .hasAnyRole("SUPER_ADMIN", "OPERATIONS_ADMIN", "CS_ADMIN")

                        // 명시하지 않은 API도 기본적으로 인증은 필요합니다.
                        .anyRequest().authenticated()
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

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Default Spring Security login is disabled.");
        };
    }
}
