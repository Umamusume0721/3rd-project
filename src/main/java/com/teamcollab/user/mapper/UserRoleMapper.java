package com.teamcollab.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamcollab.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    int deleteByUserId(@Param("userId") Long userId);
}
