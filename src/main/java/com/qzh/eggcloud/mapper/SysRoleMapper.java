package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.auth.SysRoleEntity;
import org.apache.ibatis.annotations.Param;

public interface SysRoleMapper {
    SysRoleEntity findRoleByName(@Param("roleName") String roleName);
}
