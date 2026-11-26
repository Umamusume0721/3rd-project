package com.teamcollab.security;

import com.teamcollab.common.BizException;
import com.teamcollab.common.ErrorCode;

public final class DataScope {

    private DataScope() {
    }

    public static boolean isSuperAdmin() {
        LoginUser user = UserContext.get();
        return user != null && user.isSuperAdmin();
    }

    public static void requireSelfOrAdmin(Long targetUserId) {
        LoginUser user = UserContext.require();
        if (!user.isSuperAdmin() && !user.getUserId().equals(targetUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "只能操作自己的数据");
        }
    }

    public static void requireTeamOwnerOrAdmin(Long teamOwnerId) {
        LoginUser user = UserContext.require();
        if (user.isSuperAdmin()) {
            return;
        }
        if (teamOwnerId == null || !teamOwnerId.equals(user.getUserId())) {
            throw new BizException(ErrorCode.FORBIDDEN, "只有团队负责人可以执行该操作");
        }
    }

    public static boolean isReadOnly() {
        LoginUser user = UserContext.get();
        return user != null && user.hasRole("GUEST") && !user.isSuperAdmin();
    }
}
