package com.qzh.eggcloud.auth.handler;

import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.RespUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName UserLoginFailureHandler
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 登录失败处理器
 **/
@Component
@Slf4j
public class UserLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        // 这些对于操作的处理类可以根据不同异常进行不同处理
        if (e instanceof UsernameNotFoundException) {
            log.info("【登录失败】" + e.getMessage());
            RespUtil.responseJson(httpServletResponse, RespUtil.generate(ErrorCode.AccountNotFound, null));
        }
        if (e instanceof LockedException) {
            log.info("【登录失败】" + e.getMessage());
            RespUtil.responseJson(httpServletResponse, RespUtil.generate(ErrorCode.AccountLocked, null));
        }
        if (e instanceof BadCredentialsException) {
            log.info("【登录失败】" + e.getMessage());
            RespUtil.responseJson(httpServletResponse, RespUtil.generate(ErrorCode.WrongPassword, null));
        }
        RespUtil.responseJson(httpServletResponse, RespUtil.generate(ErrorCode.Fail.getCode(), "登录失败", null));
    }
}
