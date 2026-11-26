package com.teamcollab.common;

public enum ErrorCode {

    SUCCESS(0, "成功"),

    PARAM_ERROR(40001, "参数校验失败"),

    UNAUTHORIZED(40101, "未登录或未携带 Token"),
    TOKEN_EXPIRED(40102, "Token 已失效，请重新登录"),
    ACCOUNT_OR_PWD_ERROR(40103, "账号或密码错误"),
    ACCOUNT_DISABLED(40104, "账号已被禁用"),

    FORBIDDEN(40301, "无权限访问"),
    NOT_FOUND(40401, "资源不存在"),

    CONFLICT(40902, "操作冲突：请先处理关联数据"),
    SERVER_ERROR(50000, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
