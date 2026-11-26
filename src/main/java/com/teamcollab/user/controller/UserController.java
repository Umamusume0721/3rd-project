package com.teamcollab.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teamcollab.common.R;
import com.teamcollab.security.DataScope;
import com.teamcollab.security.RequirePermission;
import com.teamcollab.security.UserContext;
import com.teamcollab.user.constant.PermCodes;
import com.teamcollab.user.dto.AssignRoleRequest;
import com.teamcollab.user.dto.UpdateUserRequest;
import com.teamcollab.user.dto.UserQuery;
import com.teamcollab.user.service.UserService;
import com.teamcollab.user.vo.UserInfoVO;
import com.teamcollab.user.vo.UserPageVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口。
 * 注意：/me 是“当前用户”，必须写在 /{id} 之外单独映射，否则会被路径变量吞掉。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 用户分页列表 */
    @GetMapping
    @RequirePermission(PermCodes.USER_LIST)
    public R<IPage<UserPageVO>> page(UserQuery query) {
        return R.ok(userService.page(query));
    }

    /** 当前登录用户 */
    @GetMapping("/me")
    public R<UserInfoVO> me() {
        return R.ok(userService.detail(UserContext.getUserId()));
    }

    /** 修改当前登录用户资料：只允许改自己 */
    @PutMapping("/me")
    public R<UserInfoVO> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = UserContext.getUserId();
        DataScope.requireSelfOrAdmin(userId);
        return R.ok(userService.updateProfile(userId, request));
    }

    /** 用户详情 */
    @GetMapping("/{id}")
    @RequirePermission(PermCodes.USER_VIEW)
    public R<UserInfoVO> detail(@PathVariable Long id) {
        return R.ok(userService.detail(id));
    }

    /** 分配角色（覆盖式） */
    @PutMapping("/{id}/roles")
    @RequirePermission(PermCodes.USER_ASSIGN_ROLE)
    public R<Void> assignRoles(@PathVariable Long id, @Valid @RequestBody AssignRoleRequest request) {
        userService.assignRoles(id, request.getRoleIds());
        return R.ok();
    }

    /** 启用 / 禁用用户 */
    @PutMapping("/{id}/status")
    @RequirePermission(PermCodes.USER_DISABLE)
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return R.ok();
    }
}
