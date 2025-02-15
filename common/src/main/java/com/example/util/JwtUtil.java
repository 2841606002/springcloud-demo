package com.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@ConfigurationProperties(prefix = "jwt.config") // 绑定配置文件中的 jwt.config 属性
public class JwtUtil {

    private String key; // 秘钥
    private long exp;   // 过期时间（毫秒）

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    /**
     * 生成 JWT
     *
     * @param id      用户 ID
     * @param subject 用户名
     * @param role    用户角色
     * @return JWT 字符串
     */
    public String createJWT(String id, String subject, String role) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key)
                .claim("role", role); // 添加角色信息
        if (exp > 0) {
            builder.setExpiration(new Date(nowMillis + exp)); // 设置过期时间
        }
        return builder.compact();
    }

    /**
     * 解析 JWT
     *
     * @param jwtStr JWT 字符串
     * @return Claims 对象
     */
    public Claims parseJWT(String jwtStr) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwtStr)
                .getBody();
    }
}