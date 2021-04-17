package com.qzh.eggcloud.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @ClassName SysUserDetail
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description user detail 与spring security 交互
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserDetail implements UserDetails, Serializable {
    private static final long serialVersionUID = 478925307349123718L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;


    /**
     * 头像
     */
    private String avatar;


    /**
     * 用户授权集合
     */
    private Collection<GrantedAuthority> authorities;

    /**
     * 账户是否过期
     */
    private boolean isAccountNonExpired = false;
    /**
     * 账户是否被锁定
     */
    private boolean isAccountNonLocked = false;
    /**
     * 证书是否过期
     */
    private boolean isCredentialsNonExpired = false;
    /**
     * 账户是否有效
     */
    private boolean isEnabled = true;


    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态:ACTIVE 1  PROHIBIT 0
     */
    private Integer status;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 签名
     */
    private String signature;


    /**
     * 注册时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;


    /**
     * 个人仓库id
     */
    private Long storeId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void clearPassword() {
        this.password = "";
    }
}
