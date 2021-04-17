package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.auth.SysMenuEntity;
import com.qzh.eggcloud.model.auth.SysRoleEntity;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.UserQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysUserService {

    SysUserEntity findByUsername(@Param("username") String username);

    SysUserEntity findByEmail(@Param("email") String email);

    Boolean isExistUsername(@Param("username") String username);

    Boolean isExistEmail(@Param("email") String email);

    List<SysRoleEntity> selectRoleByUserId(@Param("userId") Long userId);

    int saveOrUpdateUser(@Param("user") SysUserEntity user);

    Boolean registerUser(@Param("user") SysUserEntity user);

    void setUserAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    SysUserEntity findByUserId(@Param("userId") Long userId);

    int updateUserInfo(@Param("user") SysUserEntity user) throws BaseException;

    PageInfo<SysUserEntity> findUsersWithDetail(UserQuery query, PageEntity pageEntity);

    List<SysUserEntity> findUserDetails(UserQuery userQuery);

    void removeUser(@Param("userId") Long userId);

    void changeUserStatus(@Param("userId") Long userId);


    List<MenuDTO> getUserMenuTree(@Param("userId") Long userId);

    List<SysMenuEntity> getUserMenus(@Param("userId") Long userId);

    Set<String> getUserPermissions(Long userId);
}
