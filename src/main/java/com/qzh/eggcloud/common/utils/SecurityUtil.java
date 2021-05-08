package com.qzh.eggcloud.common.utils;

import com.google.common.collect.Lists;
import com.qzh.eggcloud.auth.SysUserDetail;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.util.Reflection;
import org.reflections.Reflections;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName SecurityUtil
 * @Author DiangD
 * @Date 2021/3/7
 * @Version 1.0
 * @Description SecurityUtil
 **/
@Component("SecurityUtil")
public class SecurityUtil {

    private static final String HAS_PERMISSION_REGEX = "hasPermission\\((.*)\\)";

    public static final List<String> PERMISSIONS = Lists.newArrayList();


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

    @PostConstruct
    public static void getAlAnnotationPermissions() {
        Reflections reflections = new Reflections("com.qzh.eggcloud.controller");
        Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(RestController.class);
        List<String> classList = Lists.newArrayList();
        for (Class<?> Classes : classesList) {
            //得到该类下面的所有方法
            Method[] methods = Classes.getDeclaredMethods();
            for (Method method : methods) {
                PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
                if (preAuthorize == null) {
                    continue;
                }
                if (StringUtils.isEmpty(preAuthorize.value())) {
                    continue;
                }
                String permission = null;
                Pattern pattern = Pattern.compile(HAS_PERMISSION_REGEX);
                Matcher matcher = pattern.matcher(preAuthorize.value());
                if (matcher.find()) {
                    String hasPermission = matcher.group(1);
                    String[] arr = hasPermission.split(",");
                    permission = StringUtils.substringBetween(arr[1], "'", "'");
                }

                if (classList.contains(permission)) {
                    continue;
                }

                if (StringUtils.isNotEmpty(permission)) {
                    classList.add(permission);
                }
            }
        }
        PERMISSIONS.addAll(classList.stream().sorted().collect(Collectors.toList()));
    }
}
