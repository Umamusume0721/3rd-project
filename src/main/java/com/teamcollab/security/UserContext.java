package com.teamcollab.security;

import com.teamcollab.common.BizException;
import com.teamcollab.common.ErrorCode;

/**
 * 当前请求的用户上下文（ThreadLocal 实现）。
 * 业务代码用 UserContext.getUserId() 取当前用户，不信任前端传来的 id。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static LoginUser require() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }

    public static Long getUserId() {
        return require().getUserId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
