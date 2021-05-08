package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.auth.SysRoleEntity;
import com.qzh.eggcloud.model.auth.dto.RoleDTO;
import com.qzh.eggcloud.model.query.RoleQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    SysRoleEntity findRoleByName(@Param("roleName") String roleName);

    int deleteByMenuIds(@Param("menuIds") List<Long> menuIds);

    List<RoleDTO> findByQuery(@Param("query") RoleQuery query);

    int findCountByName(@Param("roleName") String roleName);

    int insertRole(@Param("role") SysRoleEntity role);

    SysRoleEntity findByName(@Param("roleName") String roleName);

    int updateRole(@Param("role") SysRoleEntity role);

    List<Long> findMenuIdsByRoleId(@Param("roleId") Long roleId);

    int deleteById(@Param("roleId") Long roleId);

    int deleteByIds(@Param("ids") List<Long> roleIds);

    int deleteRoleMenuByRoleIds(@Param("ids") List<Long> roleIds);


    int insertRoleMenu(@Param("role") SysRoleEntity role);

}
