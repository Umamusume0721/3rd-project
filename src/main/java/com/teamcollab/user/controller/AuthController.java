package com.teamcollab.user.controller;

import com.teamcollab.common.R;
import com.teamcollab.user.dto.LoginRequest;
import com.teamcollab.user.service.AuthService;
import com.teamcollab.user.vo.LoginVO;
import com.teamcollab.user.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = authorization == null ? null
                : authorization.replace("Bearer ", "").trim();
        authService.logout(token);
        return R.ok();
    }

    @GetMapping("/me")
    public R<UserInfoVO> me() {
        return R.ok(authService.currentUserInfo());
    }
}
