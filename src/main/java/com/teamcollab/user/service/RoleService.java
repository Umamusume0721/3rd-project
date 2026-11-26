package com.teamcollab.user.service;

import com.teamcollab.common.BizException;
import com.teamcollab.common.ErrorCode;
import com.teamcollab.user.dto.AssignPermissionRequest;
import com.teamcollab.user.entity.Permission;
import com.teamcollab.user.entity.Role;
import com.teamcollab.user.entity.RolePermission;
import com.teamcollab.user.mapper.PermissionMapper;
import com.teamcollab.user.mapper.RoleMapper;
import com.teamcollab.user.mapper.RolePermissionMapper;
import com.teamcollab.user.vo.RoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final PermissionService permissionService;

    public RoleService(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper,
                       PermissionMapper permissionMapper, PermissionService permissionService) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
        this.permissionService = permissionService;
    }

    public List<RoleVO> list() {
        return roleMapper.selectList(null).stream().map(role -> {
            RoleVO vo = new RoleVO();
            vo.setId(role.getId());
            vo.setRoleCode(role.getRoleCode());
            vo.setRoleName(role.getRoleName());
            vo.setDescription(role.getDescription());
            vo.setStatus(role.getStatus());
            return vo;
        }).toList();
    }

    public List<Permission> permissionsOfRole(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, AssignPermissionRequest request) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "角色不存在");
        }
        if ("SUPER_ADMIN".equals(role.getRoleCode())) {
            throw new BizException(ErrorCode.CONFLICT, "超级管理员权限不可裁剪");
        }
        rolePermissionMapper.deleteByRoleId(roleId);
        if (request.getPermissionIds() != null) {
            request.getPermissionIds().stream().distinct().forEach(permissionId -> {
                RolePermission link = new RolePermission();
                link.setRoleId(roleId);
                link.setPermissionId(permissionId);
                rolePermissionMapper.insert(link);
            });
        }
        permissionService.clearAll();
    }
}
