package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.auth.SysRoleEntity;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.model.query.UserQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserMapper {
    int insertUser(@Param("user") SysUserEntity user);

    int updateUser(@Param("user") SysUserEntity user);

    SysUserEntity findByUsername(@Param("username") String username);

    SysUserEntity findByEmail(@Param("email") String email);

    List<SysRoleEntity> selectSysRoleByUserId(@Param("userId") Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int updateAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    SysUserEntity findByUserId(@Param("userId") Long userId);

    List<SysUserEntity> findUserDetailByUser(@Param("query") UserQuery query);

    void deleteUserById(@Param("userId") Long userId);

    void deleteSysRoleUserByUserId(@Param("userId") Long userId);
}
