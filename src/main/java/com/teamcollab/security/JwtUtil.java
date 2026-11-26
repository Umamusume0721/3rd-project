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

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expireMillis;
    private final String issuer;

    public JwtUtil(@Value("${teamcollab.jwt.secret}") String secret,
                   @Value("${teamcollab.jwt.expire-minutes:120}") long expireMinutes,
                   @Value("${teamcollab.jwt.issuer:teamcollab}") String issuer) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireMinutes * 60_000L;
        this.issuer = issuer;
    }

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
