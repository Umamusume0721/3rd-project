package com.teamcollab.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPageVO {

    private Long id;

    private String account;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;

    private String roleCodes;

    private String roleNames;

    private LocalDateTime createdAt;
}
