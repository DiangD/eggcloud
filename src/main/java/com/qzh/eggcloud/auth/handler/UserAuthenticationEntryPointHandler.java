package com.qzh.eggcloud.auth.handler;

import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.RespUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName UserAuthenticationEntryPointHandler
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 未登录处理器
 **/
@Component
public class UserAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        RespUtil.responseJson(httpServletResponse,
                RespUtil.generate(ErrorCode.NotLogin, null));
    }
}
