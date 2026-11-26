package com.teamcollab.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamcollab.common.ErrorCode;
import com.teamcollab.common.R;
import com.teamcollab.user.service.PermissionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 权限拦截器：
 * 请求进入 -> 取 Token -> 解析并校验 -> 写入用户上下文 -> 按 @RequirePermission 判定 -> 放行或 40301。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final PermissionService permissionService;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(JwtUtil jwtUtil, TokenBlacklist tokenBlacklist,
                           PermissionService permissionService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
        this.permissionService = permissionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = resolveToken(request);
        if (token == null) {
            reject(response, ErrorCode.UNAUTHORIZED, 401);
            return false;
        }

        Claims claims;
        try {
            claims = jwtUtil.parse(token);
        } catch (ExpiredJwtException e) {
            reject(response, ErrorCode.TOKEN_EXPIRED, 401);
            return false;
        } catch (Exception e) {
            reject(response, ErrorCode.UNAUTHORIZED, 401);
            return false;
        }

        // 登出后的 Token 立即失效
        if (tokenBlacklist.contains(claims.getId())) {
            reject(response, ErrorCode.TOKEN_EXPIRED, 401);
            return false;
        }

        Long userId = Long.valueOf(claims.getSubject());
        String account = claims.get("account", String.class);
        String nickname = claims.get("nickname", String.class);

        List<String> roleCodes = permissionService.getRoleCodes(userId);
        Set<String> permCodes = permissionService.getPermCodes(userId);
        boolean superAdmin = roleCodes.contains("SUPER_ADMIN");

        LoginUser loginUser = new LoginUser(userId, account, nickname, roleCodes, permCodes, superAdmin);
        UserContext.set(loginUser);

        // 注解级权限判定
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            RequirePermission required = method.getMethodAnnotation(RequirePermission.class);
            if (required == null) {
                required = method.getBeanType().getAnnotation(RequirePermission.class);
            }
            if (required != null && !hasAnyPermission(loginUser, required.value())) {
                reject(response, ErrorCode.FORBIDDEN, 403);
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 必须清理，否则线程复用时上下文会串号
        UserContext.clear();
    }

    private boolean hasAnyPermission(LoginUser loginUser, String[] codes) {
        if (codes == null || codes.length == 0) {
            return true;
        }
        return Arrays.stream(codes).anyMatch(loginUser::hasPermission);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        if (header.startsWith(BEARER)) {
            String token = header.substring(BEARER.length()).trim();
            return token.isEmpty() ? null : token;
        }
        return header.trim();
    }

    private void reject(HttpServletResponse response, ErrorCode errorCode, int httpStatus) throws IOException {
        response.setStatus(httpStatus);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(R.fail(errorCode)));
    }
}
