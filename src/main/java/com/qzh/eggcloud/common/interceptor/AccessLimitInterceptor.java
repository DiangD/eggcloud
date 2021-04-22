package com.qzh.eggcloud.common.interceptor;

import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.annotation.AccessLimit;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AccessLimitInterceptor
 * @Author DiangD
 * @Date 2021/3/7
 * @Version 1.0
 * @Description access limiter
 **/
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    private static final String ACCESS_LIMIT_KEY_PREFIX = "ACCESS:LIMIT:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            //没有使用注解
            if (accessLimit == null) {
                return true;
            }
            //获取注解的值
            int limit = accessLimit.limit();
            int timeScope = accessLimit.timeScope();
            TimeUnit unit = accessLimit.timeUnit();
            String uri = request.getRequestURI();
            final String key;
            //获取登录用户
            if (SecurityUtil.isLogin()) {
                SysUserDetail user = SecurityUtil.getSysUserDetail();
                //prefix:user_id:uri
                assert user != null;
                key = ACCESS_LIMIT_KEY_PREFIX + user.getUserId() + ":" + uri;

            } else {
                //prefix:remote_addr:uri
                key = ACCESS_LIMIT_KEY_PREFIX + request.getRemoteAddr().
                        replaceAll(":", "") + ":" + uri;
            }
            String currentCount = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotEmpty(currentCount)) {
                int count = Integer.parseInt(currentCount);
                if (count < limit) {
                    redisTemplate.opsForValue().increment(key, 1);
                    return true;
                }
                throw new BaseException(ErrorCode.FrequentOperation);
            } else {
                redisTemplate.opsForValue().set(key, "1", timeScope, unit);
                return true;
            }
        }
        return true;
    }
}

