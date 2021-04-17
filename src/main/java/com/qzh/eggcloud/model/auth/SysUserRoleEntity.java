package com.qzh.eggcloud.model.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName SysUserRoleEntity
 * @Author DiangD
 * @Date 2021/3/5
 * @Version 1.0
 * @Description user role domain
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SysUserRoleEntity implements Serializable {
    private static final long serialVersionUID = 9127256637205780711L;
    /**
     * id
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色ID
     */
    private Long roleId;
}
