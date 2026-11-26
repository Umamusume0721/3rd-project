package com.teamcollab.security;

import com.teamcollab.common.BizException;
import com.teamcollab.common.ErrorCode;

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
