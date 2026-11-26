package com.teamcollab.user.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class LoginVO {

    private String token;

    private String tokenType = "Bearer";

    private long expiresIn;

    private Long userId;

    private String account;

    private String nickname;

    private List<String> roles;

    private Set<String> permissions;
}
