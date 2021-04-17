package com.qzh.eggcloud.auth.handler;

import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.RespUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName UserLogoutSuccessHandler
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 登出成功处理器
 **/
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        RespUtil.responseJson(httpServletResponse, RespUtil.generate(ErrorCode.Success.getCode(), "登出成功", null));
    }
}
