package com.teamcollab.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPageVO {

    private Long id;

    private String account;

    private String nickname;

    private String email;

    /** 已脱敏 */
    private String phone;

    private Integer status;

    /** 逗号分隔的角色编码，如 TEAM_OWNER */
    private String roleCodes;

    /** 逗号分隔的角色名称，如 团队负责人 */
    private String roleNames;

    private LocalDateTime createdAt;
}
