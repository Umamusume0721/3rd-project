package com.teamcollab.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamcollab.user.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    List<String> selectPermCodesByUserId(@Param("userId") Long userId);

    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    List<Permission> selectAllOrdered();
}
