package com.teamcollab.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

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

            blacklist.remove(jti);
            return false;
        }
        return true;
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }

    public int size() {
        return blacklist.size();
    }
}
