package com.qzh.eggcloud.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

/**
 * @ClassName LogAspect
 * @Author DiangD
 * @Date 2021/3/19
 * @Version 1.0
 * @Description 统一日志处理
 **/
@Aspect
@Slf4j
@Component
public class LogAspect {
    @Pointcut("@annotation(com.qzh.eggcloud.common.annotation.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long begin = Instant.now().toEpochMilli();
        Object res = point.proceed();
        long end = Instant.now().toEpochMilli();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        log.info("url:{},ip:{},className:{},method:{},args:{},response:{},time:{}ms",
                request.getRequestURI(), request.getRemoteAddr(),
                point.getTarget().getClass().getName(),
                point.getSignature().getName(),
                Arrays.asList(point.getArgs()),
                res,
                (end - begin));
        return res;
    }
}

