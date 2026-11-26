package com.teamcollab.user.constant;

/**
 * 权限点常量。
 * 与 sys_permission.code 一一对应；接口上写常量而不是硬编码字符串，改名字只需改这里。
 */
public final class PermCodes {

    private PermCodes() {
    }

    // ---- 用户与权限（C 组）----
    public static final String USER_LIST = "user:list";
    public static final String USER_VIEW = "user:view";
    public static final String USER_ASSIGN_ROLE = "user:assign-role";
    public static final String USER_DISABLE = "user:disable";
    public static final String ROLE_LIST = "role:list";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_ASSIGN_PERMISSION = "role:assign-permission";

    // ---- 核心业务（D 组，供王浩宇复用）----
    public static final String TEAM_CREATE = "team:create";
    public static final String TEAM_UPDATE = "team:update";
    public static final String TEAM_INVITE = "team:invite";
    public static final String TEAM_REMOVE_MEMBER = "team:remove-member";
    public static final String TEAM_TRANSFER = "team:transfer";
    public static final String PROJECT_CREATE = "project:create";
    public static final String PROJECT_UPDATE = "project:update";
    public static final String PROJECT_DELETE = "project:delete";
    public static final String TASK_CREATE = "task:create";
    public static final String TASK_UPDATE = "task:update";
    public static final String TASK_ASSIGN = "task:assign";
    public static final String TASK_DELETE = "task:delete";
    public static final String COMMENT_CREATE = "comment:create";

    // ---- 角色编码（业务判定用）----
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_TEAM_OWNER = "TEAM_OWNER";
    public static final String ROLE_MEMBER = "MEMBER";
    public static final String ROLE_GUEST = "GUEST";
}
