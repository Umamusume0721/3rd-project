package com.teamcollab.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teamcollab.user.dto.UserQuery;
import com.teamcollab.user.entity.User;
import com.teamcollab.user.vo.UserPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 按账号查用户（登录用）。手写 SQL 不走逻辑删除插件，条件里显式带 deleted = 0。
     */
    User selectByAccount(@Param("account") String account);

    /**
     * 用户分页查询：支持关键字（账号/昵称）、状态、角色筛选，并带出角色名称。
     */
    IPage<UserPageVO> selectUserPage(IPage<UserPageVO> page, @Param("q") UserQuery query);
}
