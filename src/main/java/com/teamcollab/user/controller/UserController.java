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

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RequirePermission(PermCodes.USER_LIST)
    public R<IPage<UserPageVO>> page(UserQuery query) {
        return R.ok(userService.page(query));
    }

    @GetMapping("/me")
    public R<UserInfoVO> me() {
        return R.ok(userService.detail(UserContext.getUserId()));
    }

    @PutMapping("/me")
    public R<UserInfoVO> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = UserContext.getUserId();
        DataScope.requireSelfOrAdmin(userId);
        return R.ok(userService.updateProfile(userId, request));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermCodes.USER_VIEW)
    public R<UserInfoVO> detail(@PathVariable Long id) {
        return R.ok(userService.detail(id));
    }

    @PutMapping("/{id}/roles")
    @RequirePermission(PermCodes.USER_ASSIGN_ROLE)
    public R<Void> assignRoles(@PathVariable Long id, @Valid @RequestBody AssignRoleRequest request) {
        userService.assignRoles(id, request.getRoleIds());
        return R.ok();
    }

    @PutMapping("/{id}/status")
    @RequirePermission(PermCodes.USER_DISABLE)
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return R.ok();
    }
}
