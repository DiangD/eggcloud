package com.qzh.eggcloud.auth.handler;

import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.RespUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName UserAuthAccessDeniedHandler
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 无权限处理器
 **/
@Component
public class UserAuthAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        RespUtil.responseJson(httpServletResponse, RespUtil.generate(ErrorCode.Unauthorized, null));
    }
}
