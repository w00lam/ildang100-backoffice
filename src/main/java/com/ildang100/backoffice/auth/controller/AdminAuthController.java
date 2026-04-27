package com.ildang100.backoffice.auth.controller;

import com.ildang100.backoffice.auth.dto.AdminSignUpRequest;
import com.ildang100.backoffice.auth.service.AdminService;
import com.ildang100.backoffice.common.response.CommonApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 인증 관련 API를 담당하는 컨트롤러입니다.
 *
 * <p>
 * 관리자 회원가입 등의 인증 관련 기능을 제공합니다.
 * 요청 데이터는 DTO를 통해 전달되며, {@code @Valid}를 통해 유효성 검증이 수행됩니다.
 * </p>
 *
 * <p><b>현재 제공 기능</b></p>
 * <ul>
 *     <li>관리자 회원가입</li>
 * </ul>
 *
 * @author 이우람
 * @since 2026-04-27
 */
@RestController
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;

    @PostMapping("/admins/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonApiResponse<Void> signup(@RequestBody @Valid AdminSignUpRequest request) {
        adminService.signUp(request);

        return CommonApiResponse.success(HttpStatus.CREATED, "관리자 회원가입이 완료되었습니다.", null);
    }
}
