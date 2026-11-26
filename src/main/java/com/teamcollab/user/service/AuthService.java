package com.teamcollab.user.service;

import com.teamcollab.common.BizException;
import com.teamcollab.common.ErrorCode;
import com.teamcollab.security.JwtUtil;
import com.teamcollab.security.LoginUser;
import com.teamcollab.security.TokenBlacklist;
import com.teamcollab.security.UserContext;
import com.teamcollab.user.dto.LoginRequest;
import com.teamcollab.user.entity.User;
import com.teamcollab.user.mapper.RoleMapper;
import com.teamcollab.user.mapper.UserMapper;
import com.teamcollab.user.vo.LoginVO;
import com.teamcollab.user.vo.UserInfoVO;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 登录 / 登出 / 当前用户信息。
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    public AuthService(UserMapper userMapper, RoleMapper roleMapper, PermissionService permissionService,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenBlacklist tokenBlacklist) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionService = permissionService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }

    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectByAccount(request.getAccount().trim());

        // 账号不存在与密码错误返回同一提示，避免被用来枚举账号
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("登录失败：account={}", request.getAccount());
            throw new BizException(ErrorCode.ACCOUNT_OR_PWD_ERROR);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ErrorCode.ACCOUNT_DISABLED);
        }

        String token = jwtUtil.generate(user.getId(), user.getAccount(), user.getNickname());
        List<String> roles = permissionService.getRoleCodes(user.getId());
        Set<String> permissions = permissionService.getPermCodes(user.getId());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setExpiresIn(jwtUtil.getExpireSeconds());
        vo.setUserId(user.getId());
        vo.setAccount(user.getAccount());
        vo.setNickname(user.getNickname());
        vo.setRoles(roles);
        vo.setPermissions(permissions);
        log.info("登录成功：account={}", user.getAccount());
        return vo;
    }

    /**
     * 登出：把当前 Token 的 jti 加入失效名单，有效期到该 Token 自然过期为止。
     */
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            Claims claims = jwtUtil.parse(token);
            tokenBlacklist.add(claims.getId(), claims.getExpiration().getTime());
        } catch (Exception e) {
            // Token 本身已失效，登出直接成功
            log.debug("登出时 Token 已失效：{}", e.getMessage());
        }
    }

    public UserInfoVO currentUserInfo() {
        LoginUser loginUser = UserContext.require();
        User user = userMapper.selectById(loginUser.getUserId());
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setAccount(user.getAccount());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setRoles(permissionService.getRoleCodes(user.getId()));
        vo.setPermissions(permissionService.getPermCodes(user.getId()));
        return vo;
    }

    /**
     * 手机号脱敏：13800000002 -> 138****0002
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
