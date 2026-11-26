package com.teamcollab.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具：签发与解析。
 * 注意：JWT 本身无状态，登出必须靠 TokenBlacklist 记录失效 Token。
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expireMillis;
    private final String issuer;

    public JwtUtil(@Value("${teamcollab.jwt.secret}") String secret,
                   @Value("${teamcollab.jwt.expire-minutes:120}") long expireMinutes,
                   @Value("${teamcollab.jwt.issuer:teamcollab}") String issuer) {
        // HS256 要求密钥长度 >= 32 字节
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireMinutes * 60_000L;
        this.issuer = issuer;
    }

    /**
     * 签发 Token。jti 用于登出时加入失效名单。
     */
    public String generate(Long userId, String account, String nickname) {
        Date now = new Date();
        long expireAt = now.getTime() + expireMillis;
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .claim("account", account)
                .claim("nickname", nickname)
                .issuedAt(now)
                .expiration(new Date(expireAt))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token，失败抛 JwtException（过期抛 ExpiredJwtException）。
     */
    public Claims parse(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpireSeconds() {
        return expireMillis / 1000;
    }
}
