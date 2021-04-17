package com.qzh.eggcloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.config.security.JWTConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName JWTTokenUtil
 * @Author DiangD
 * @Date 2021/3/28
 * @Version 1.0
 * @Description JWT工具类
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTTokenUtil {

    public static String createAccessToken(SysUserDetail user) {
        return Jwts.builder()
                // 放入用户名和用户ID
                .setId(String.valueOf(user.getUserId()))
                // 主体
                .setSubject(user.getUsername())
                // 签发时间
                .setIssuedAt(new Date())
                //签发者
                .setIssuer("DiangD")
                //自定义属性
                .claim("authorities", JSON.toJSONString(user.getAuthorities()))
                .claim("storeId", user.getStoreId())
                .claim("email", user.getEmail())
                .claim("status", user.getStatus())
                .setIssuedAt(new Date())
                //过期时间
                .setExpiration(new Date(System.currentTimeMillis() + JWTConfig.expiration))
                //签名算法和密钥
                .signWith(SignatureAlgorithm.HS256, JWTConfig.secret)
                .compact();
    }

    public static String getUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    public static Boolean isExpire(String token) {
        return getTokenBody(token).getExpiration().before(new Date());
    }

    private static Claims getTokenBody(String token) {
        return Jwts.parser()
                .setSigningKey(JWTConfig.secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
