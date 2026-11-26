package com.teamcollab.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamcollab.common.BizException;
import com.teamcollab.common.ErrorCode;
import com.teamcollab.user.dto.UpdateUserRequest;
import com.teamcollab.user.dto.UserQuery;
import com.teamcollab.user.entity.Role;
import com.teamcollab.user.entity.User;
import com.teamcollab.user.entity.UserRole;
import com.teamcollab.user.mapper.RoleMapper;
import com.teamcollab.user.mapper.UserMapper;
import com.teamcollab.user.mapper.UserRoleMapper;
import com.teamcollab.user.vo.UserInfoVO;
import com.teamcollab.user.vo.UserPageVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理：分页查询、资料修改、角色分配、启用禁用。
 */
@Service
public class UserService {

    private static final long MAX_PAGE_SIZE = 100L;

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PermissionService permissionService;

    public UserService(UserMapper userMapper, UserRoleMapper userRoleMapper,
                       RoleMapper roleMapper, PermissionService permissionService) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.permissionService = permissionService;
    }

    public IPage<UserPageVO> page(UserQuery query) {
        // 页码与页大小都要兜底，防止 pageSize=100000 把库拖死
        long pageNo = Math.max(query.getPage(), 1L);
        long pageSize = Math.min(Math.max(query.getSize(), 1L), MAX_PAGE_SIZE);
        if (query.getKeyword() != null) {
            query.setKeyword(query.getKeyword().trim());
        }
        Page<UserPageVO> page = new Page<>(pageNo, pageSize);
        IPage<UserPageVO> result = userMapper.selectUserPage(page, query);
        result.getRecords().forEach(vo -> vo.setPhone(AuthService.maskPhone(vo.getPhone())));
        return result;
    }

    public UserInfoVO detail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setAccount(user.getAccount());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(AuthService.maskPhone(user.getPhone()));
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setRoles(permissionService.getRoleCodes(userId));
        vo.setPermissions(permissionService.getPermCodes(userId));
        return vo;
    }

    public UserInfoVO updateProfile(Long userId, UpdateUserRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            User exist = userMapper.selectOne(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>lambdaQuery()
                            .eq(User::getEmail, request.getEmail()));
            if (exist != null) {
                throw new BizException(ErrorCode.CONFLICT, "邮箱已被占用");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        userMapper.updateById(user);
        return detail(userId);
    }

    /**
     * 分配角色：先校验角色是否合法，再整体覆盖（先删后插）。
     */
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        if (roles.size() != roleIds.stream().distinct().count()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "存在无效的角色 ID");
        }

        userRoleMapper.deleteByUserId(userId);
        List<UserRole> links = new ArrayList<>();
        for (Long roleId : roleIds.stream().distinct().toList()) {
            UserRole link = new UserRole();
            link.setUserId(userId);
            link.setRoleId(roleId);
            links.add(link);
        }
        links.forEach(userRoleMapper::insert);
        permissionService.evict(userId);
    }

    public void updateStatus(Long userId, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "状态只能是 0 或 1");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        permissionService.evict(userId);
    }

    public List<String> roleNamesOf(Long userId) {
        return roleMapper.selectRolesByUserId(userId).stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
}
