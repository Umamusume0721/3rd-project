package com.teamcollab.security;

import com.teamcollab.common.BizException;
import com.teamcollab.common.ErrorCode;

/**
 * 数据级权限判定（功能权限之外的“这条数据你能不能动”）。
 * 团队 / 项目 / 任务的归属字段由 D 组（王浩宇）的表提供，这里只提供判定入口，
 * 业务模块把 ownerId 传进来即可，避免各写一套 if 判断。
 */
public final class DataScope {

    private DataScope() {
    }

    /** 当前用户是否为系统管理员 */
    public static boolean isSuperAdmin() {
        LoginUser user = UserContext.get();
        return user != null && user.isSuperAdmin();
    }

    /** 只能操作自己：改资料、改密码这类接口用 */
    public static void requireSelfOrAdmin(Long targetUserId) {
        LoginUser user = UserContext.require();
        if (!user.isSuperAdmin() && !user.getUserId().equals(targetUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "只能操作自己的数据");
        }
    }

    /** 团队负责人或系统管理员才能管理该团队；其余成员抛 40301 */
    public static void requireTeamOwnerOrAdmin(Long teamOwnerId) {
        LoginUser user = UserContext.require();
        if (user.isSuperAdmin()) {
            return;
        }
        if (teamOwnerId == null || !teamOwnerId.equals(user.getUserId())) {
            throw new BizException(ErrorCode.FORBIDDEN, "只有团队负责人可以执行该操作");
        }
    }

    /** 是否只读角色（访客）：用于写操作前的兜底判断 */
    public static boolean isReadOnly() {
        LoginUser user = UserContext.get();
        return user != null && user.hasRole("GUEST") && !user.isSuperAdmin();
    }
}
