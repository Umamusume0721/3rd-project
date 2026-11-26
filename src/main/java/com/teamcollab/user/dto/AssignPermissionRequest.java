package com.teamcollab.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionRequest {

    /** 允许为空：清空该角色的全部权限 */
    private List<Long> permissionIds;
}
