package com.teamcollab.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 失效名单（内存版）。
 * 原型阶段够用；上线可换成 Redis：SET blacklist:{jti} 1 EX 剩余秒数。
 */
@Component
public class TokenBlacklist {

    /** jti -> 该 Token 的过期时间戳（毫秒） */
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void add(String jti, long expireAtMillis) {
        if (jti == null || jti.isEmpty()) {
            return;
        }
        cleanExpired();
        blacklist.put(jti, expireAtMillis);
    }

    public boolean contains(String jti) {
        if (jti == null) {
            return false;
        }
        Long expireAt = blacklist.get(jti);
        if (expireAt == null) {
            return false;
        }
        if (expireAt < System.currentTimeMillis()) {
            // 已过了自然过期时间，顺手清掉
            blacklist.remove(jti);
            return false;
        }
        return true;
    }

    /**
     * 清理已过期的条目，避免内存只增不减。
     */
    private void cleanExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }

    public int size() {
        return blacklist.size();
    }
}
