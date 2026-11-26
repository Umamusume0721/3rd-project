package com.teamcollab.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamcollab.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查用户的角色编码列表（登录与拦截器都要用）。
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 查用户的完整角色对象（返回给前端的角色选择器用）。
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
}
