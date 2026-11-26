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

    User selectByAccount(@Param("account") String account);

    IPage<UserPageVO> selectUserPage(IPage<UserPageVO> page, @Param("q") UserQuery query);
}
