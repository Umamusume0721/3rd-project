package com.teamcollab.user.service;

import com.teamcollab.user.entity.Permission;
import com.teamcollab.user.mapper.PermissionMapper;
import com.teamcollab.user.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PermissionService {

    private static final long CACHE_TTL_MILLIS = 5 * 60 * 1000L;

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    private final Map<Long, CacheEntry> roleCache = new ConcurrentHashMap<>();
    private final Map<Long, CacheEntry> permCache = new ConcurrentHashMap<>();

    public PermissionService(RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    public List<String> getRoleCodes(Long userId) {
        CacheEntry cached = roleCache.get(userId);
        if (cached != null && cached.expireAt > System.currentTimeMillis()) {
            return cached.roles;
        }
        List<String> roles = roleMapper.selectRoleCodesByUserId(userId);
        roleCache.put(userId, new CacheEntry(roles, null, System.currentTimeMillis() + CACHE_TTL_MILLIS));
        return roles;
    }

    public Set<String> getPermCodes(Long userId) {
        CacheEntry cached = permCache.get(userId);
        if (cached != null && cached.expireAt > System.currentTimeMillis()) {
            return cached.perms;
        }
        List<String> codes = permissionMapper.selectPermCodesByUserId(userId);
        Set<String> perms = codes == null ? Collections.emptySet() : new HashSet<>(codes);
        permCache.put(userId, new CacheEntry(null, perms, System.currentTimeMillis() + CACHE_TTL_MILLIS));
        return perms;
    }

    public void evict(Long userId) {
        if (userId != null) {
            roleCache.remove(userId);
            permCache.remove(userId);
        }
    }

    public void clearAll() {
        roleCache.clear();
        permCache.clear();
    }

    public List<Permission> getAllPermissions() {
        return permissionMapper.selectAllOrdered();
    }

    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }

    private static final class CacheEntry {
        private final List<String> roles;
        private final Set<String> perms;
        private final long expireAt;

        private CacheEntry(List<String> roles, Set<String> perms, long expireAt) {
            this.roles = roles;
            this.perms = perms;
            this.expireAt = expireAt;
        }
    }
}
