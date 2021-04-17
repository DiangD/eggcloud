package com.qzh.eggcloud.auth.service;

import cn.hutool.core.util.ReUtil;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserDetailService
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description SysUserDetailService
 **/
@Service(value = "SysUserDetailService")
public class SysUserDetailService implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public SysUserDetail loadUserByUsername(String key) throws UsernameNotFoundException {
        String reg = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        SysUserEntity userEntity;
        //email
        if (ReUtil.isMatch(reg, key)) {
            userEntity = sysUserService.findByEmail(key);
        } else {
            userEntity = sysUserService.findByUsername(key);
        }
        if (userEntity != null) {
            SysUserDetail userDetail = new SysUserDetail();
            BeanUtils.copyProperties(userEntity, userDetail);
            return userDetail;
        }
        return null;
    }
}
