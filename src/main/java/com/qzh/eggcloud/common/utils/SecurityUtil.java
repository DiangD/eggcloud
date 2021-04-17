package com.qzh.eggcloud.common.utils;

import com.qzh.eggcloud.auth.SysUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @ClassName SecurityUtil
 * @Author DiangD
 * @Date 2021/3/7
 * @Version 1.0
 * @Description SecurityUtil
 **/
@Component("SecurityUtil")
public class SecurityUtil {
    /**
     * @return 用户是否登录
     */
    public static boolean isLogin() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser");
    }

    /**
     * @return 当前登录用户
     */
    public static SysUserDetail getSysUserDetail() {
        return (SysUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * @param userId userId
     * @return 是否是当前的登录用户
     */
    public static boolean isLoginUser(Long userId) {
        SysUserDetail loginUser = getSysUserDetail();
        assert loginUser != null;
        return userId.longValue() == loginUser.getUserId().longValue();
    }
}
