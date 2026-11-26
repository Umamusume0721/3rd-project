package com.teamcollab.user.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class UserInfoVO {

    private Long id;

    private String account;

    private String nickname;

    private String email;

    /** 已脱敏，如 138****0002 */
    private String phone;

    private Integer status;

    private List<String> roles;

    private Set<String> permissions;

    private LocalDateTime createdAt;
}
