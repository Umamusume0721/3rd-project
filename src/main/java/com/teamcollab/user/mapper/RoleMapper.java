package com.teamcollab.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamcollab.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    List<Role> selectRolesByUserId(@Param("userId") Long userId);
}
