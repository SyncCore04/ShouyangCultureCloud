package com.shouyang.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 提供 Token 的生成、解析、过期判断等功能
 *
 * @author shouyang
 */
@Component
public class JwtUtils {

    /**
     * JWT 密钥（从配置文件读取，默认值用于开发环境）
     * 生产环境务必修改为复杂的随机字符串
     */
    @Value("${jwt.secret:shouyang-culture-tourism-secret-key-2024-very-long-and-safe}")
    private String secret;

    /**
     * Token 过期时间（毫秒），从配置文件读取，默认 24 小时
     */
    @Value("${jwt.expire:86400000}")
    private Long expire;

    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 请求头中存放 Token 的字段名
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 根据用户信息生成 JWT Token
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @param userType 用户类型（user/admin）
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String username, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("userType", userType);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，获取 Claims（包含用户信息）
     *
     * @param token JWT Token
     * @return Claims 声明信息，解析失败返回 null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Token 无效、过期、签名错误等都返回 null
            return null;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get("userId");
        if (userId == null) {
            return null;
        }
        return Long.valueOf(userId.toString());
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object username = claims.get("username");
        return username == null ? null : username.toString();
    }

    /**
     * 从 Token 中获取用户类型
     */
    public String getUserType(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userType = claims.get("userType");
        return userType == null ? null : userType.toString();
    }

    /**
     * 判断 Token 是否过期
     *
     * @param token JWT Token
     * @return true=已过期或无效，false=有效
     */
    public boolean isExpired(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return true;
        }
        return claims.getExpiration().before(new Date());
    }

    /**
     * 验证 Token 是否有效（未过期且能正常解析）
     */
    public boolean validateToken(String token) {
        return parseToken(token) != null && !isExpired(token);
    }

    /**
     * 获取密钥对象
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
