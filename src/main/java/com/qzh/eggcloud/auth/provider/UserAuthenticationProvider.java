package com.qzh.eggcloud.auth.provider;

import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.auth.service.SysUserDetailService;
import com.qzh.eggcloud.model.auth.SysRoleEntity;
import com.qzh.eggcloud.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName UserAuthenticationProvider
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 认证处理逻辑
 **/
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    @Qualifier(value = "SysUserDetailService")
    private SysUserDetailService systemUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginKey = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        SysUserDetail userDetail = systemUserDetailService.loadUserByUsername(loginKey);
        if (userDetail == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        if (!passwordEncoder.matches(password, userDetail.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }
        // 还可以加一些其他信息的判断，比如用户账号已停用等判断
        if (userDetail.getStatus() == 0) {
            userDetail.setAccountNonLocked(true);
            throw new LockedException("该用户已被冻结");
        }
        // 角色集合
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 查询用户角色
        List<SysRoleEntity> sysRoleEntities = sysUserService.selectRoleByUserId(userDetail.getUserId());
        for (SysRoleEntity sysRoleEntity : sysRoleEntities) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + sysRoleEntity.getRoleName()));
        }
        userDetail.setAuthorities(authorities);
        // 进行登录
        return new UsernamePasswordAuthenticationToken(userDetail, password, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
