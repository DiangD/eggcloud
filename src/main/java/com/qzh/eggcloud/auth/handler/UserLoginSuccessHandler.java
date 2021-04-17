package com.qzh.eggcloud.auth.handler;

import com.google.common.collect.Maps;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.config.security.JWTConfig;
import com.qzh.eggcloud.common.utils.JWTTokenUtil;
import com.qzh.eggcloud.common.utils.RespUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName UserLoginSuccessHandler
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 登陆成功处理器
 **/
@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        SysUserDetail userDetail = (SysUserDetail) authentication.getPrincipal();
        String token = JWTConfig.tokenPrefix + JWTTokenUtil.createAccessToken(userDetail);
        Map<String, Object> result = Maps.newHashMap();
        result.put("token", token);
        result.put("user", userDetail);
        RespUtil.responseJson(httpServletResponse, RespUtil.success(result));
    }
}
