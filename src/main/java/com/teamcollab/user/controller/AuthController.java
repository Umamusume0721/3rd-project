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

/**
 * 登录 / 登出 / 当前用户。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 登录：白名单接口，不需要 Token */
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request));
    }

    /** 登出：把当前 Token 加入失效名单 */
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = authorization == null ? null
                : authorization.replace("Bearer ", "").trim();
        authService.logout(token);
        return R.ok();
    }

    /** 当前登录用户信息（含角色与权限点，前端据此控制按钮显隐） */
    @GetMapping("/me")
    public R<UserInfoVO> me() {
        return R.ok(authService.currentUserInfo());
    }
}
