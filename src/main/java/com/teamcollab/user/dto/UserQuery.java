package com.teamcollab.user.dto;

import lombok.Data;

@Data
public class UserQuery {

    /** 账号或昵称模糊搜索 */
    private String keyword;

    /** 1 启用 0 禁用，null 表示全部 */
    private Integer status;

    /** 按角色编码筛选，如 TEAM_OWNER */
    private String roleCode;

    private long page = 1;

    private long size = 10;
}
