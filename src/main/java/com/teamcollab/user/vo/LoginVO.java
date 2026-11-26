package com.teamcollab.user.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class LoginVO {

    private String token;

    /** 固定 Bearer，前端拼接 Authorization: Bearer {token} */
    private String tokenType = "Bearer";

    /** Token 有效期（秒） */
    private long expiresIn;

    private Long userId;

    private String account;

    private String nickname;

    private List<String> roles;

    private Set<String> permissions;
}
