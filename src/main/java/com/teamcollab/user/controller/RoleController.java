package com.teamcollab.user.controller;

import com.teamcollab.common.R;
import com.teamcollab.security.RequirePermission;
import com.teamcollab.user.constant.PermCodes;
import com.teamcollab.user.dto.AssignPermissionRequest;
import com.teamcollab.user.entity.Permission;
import com.teamcollab.user.service.PermissionService;
import com.teamcollab.user.service.RoleService;
import com.teamcollab.user.vo.RoleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping("/roles")
    @RequirePermission(PermCodes.ROLE_LIST)
    public R<List<RoleVO>> roles() {
        return R.ok(roleService.list());
    }

    @GetMapping("/permissions")
    @RequirePermission(PermCodes.ROLE_LIST)
    public R<List<Permission>> permissions() {
        return R.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/roles/{id}/permissions")
    @RequirePermission(PermCodes.ROLE_LIST)
    public R<List<Permission>> rolePermissions(@PathVariable Long id) {
        return R.ok(roleService.permissionsOfRole(id));
    }

    @PutMapping("/roles/{id}/permissions")
    @RequirePermission(PermCodes.ROLE_ASSIGN_PERMISSION)
    public R<Void> assignPermissions(@PathVariable Long id,
                                     @RequestBody AssignPermissionRequest request) {
        roleService.assignPermissions(id, request);
        return R.ok();
    }
}
