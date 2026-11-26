package com.teamcollab.user.constant;

public final class PermCodes {

    private PermCodes() {
    }

    public static final String USER_LIST = "user:list";
    public static final String USER_VIEW = "user:view";
    public static final String USER_ASSIGN_ROLE = "user:assign-role";
    public static final String USER_DISABLE = "user:disable";
    public static final String ROLE_LIST = "role:list";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_ASSIGN_PERMISSION = "role:assign-permission";

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

    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_TEAM_OWNER = "TEAM_OWNER";
    public static final String ROLE_MEMBER = "MEMBER";
    public static final String ROLE_GUEST = "GUEST";
}
