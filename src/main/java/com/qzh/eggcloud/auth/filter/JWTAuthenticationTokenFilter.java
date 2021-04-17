package com.qzh.eggcloud.auth.filter;

import com.alibaba.fastjson.JSONObject;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.config.security.JWTConfig;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.sun.org.apache.regexp.internal.RE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JWTAuthenticationTokenFilter
 * @Author DiangD
 * @Date 2021/3/28
 * @Version 1.0
 * @Description JWT接口请求校验拦截器
 * 请求接口时会进入这里验证Token是否合法和过期
 **/
@Slf4j
public class JWTAuthenticationTokenFilter extends BasicAuthenticationFilter {
    public JWTAuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenHeader = request.getHeader(JWTConfig.tokenHeader);
        if (StringUtils.isEmpty(tokenHeader)) {
            tokenHeader = request.getParameter("token");
        }
        if (!StringUtils.isEmpty(tokenHeader) && tokenHeader.startsWith(JWTConfig.tokenPrefix)) {
            try {
                String token = tokenHeader.replace(JWTConfig.tokenPrefix, "");
                Claims claims = Jwts.parser()
                        .setSigningKey(JWTConfig.secret)
                        .parseClaimsJws(token)
                        .getBody();
                String username = claims.getSubject();
                String userId = claims.getId();
                if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(userId)) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    String email = claims.get("email").toString();
                    Long storeId = claims.get("storeId", Long.class);
                    Integer status = claims.get("status", Integer.class);
                    String authority = claims.get("authorities").toString();
                    if (!StringUtils.isEmpty(authority)) {
                        List<Map<String, String>> authorityMap = JSONObject.parseObject(authority, List.class);
                        for (Map<String, String> role : authorityMap) {
                            if (role != null) {
                                authorities.add(new SimpleGrantedAuthority(role.get("authority")));
                            }
                        }
                    }
                    SysUserDetail systemUser = SysUserDetail.builder()
                            .userId(Long.parseLong(userId))
                            .username(username)
                            .authorities(authorities)
                            .email(email)
                            .storeId(storeId)
                            .isAccountNonExpired(false)
                            .isAccountNonLocked(false)
                            .isCredentialsNonExpired(false)
                            .isEnabled(true)
                            .status(status)
                            .build();
                    if (status == 0) {
                        systemUser.setAccountNonLocked(true);
                    }
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(systemUser, userId, authorities);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (ExpiredJwtException e) {
                log.info("Token过期");
                RespUtil.responseJson(response,
                        RespUtil.generate(ErrorCode.TokenExpired, null));
            } catch (Exception e) {
                log.info("Token无效");
                RespUtil.responseJson(response,
                        RespUtil.generate(ErrorCode.InvalidToken, null));
            }
        }
        chain.doFilter(request, response);
    }
}
