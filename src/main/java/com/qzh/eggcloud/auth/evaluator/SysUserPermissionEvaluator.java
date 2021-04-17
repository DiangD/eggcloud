package com.qzh.eggcloud.auth.evaluator;

import com.google.common.collect.Sets;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.model.auth.SysMenuEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SysUserPermissionEvaluator
 * @Author DiangD
 * @Date 2021/4/1
 * @Version 1.0
 * @Description 自定义权限注解验证
 **/
@Component
public class SysUserPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private SysUserService sysUserService;

    /**
     * hasPermission鉴权方法
     * 这里仅仅判断PreAuthorize注解中的权限表达式
     * 实际中可以根据业务需求设计数据库通过targetUrl和permission做更复杂鉴权
     * 当然targetUrl不一定是URL可以是数据Id还可以是管理员标识等,这里根据需求自行设计
     *
     * @param authentication 用户身份(在使用hasPermission表达式时Authentication参数默认会自动带上)
     * @param targetUrl      请求路径
     * @param permission     请求路径权限
     * @return boolean 是否通过
     * @Author Sans
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object permission) {
        SysUserDetail userDetail = (SysUserDetail) authentication.getPrincipal();
        Set<String> permissions = sysUserService.getUserPermissions(userDetail.getUserId());
        return permissions.contains(permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

}
