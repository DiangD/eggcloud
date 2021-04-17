package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.auth.SysMenuEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysMenuMapper {
    List<MenuDTO> findMenuTreeByRoleIds(@Param("roleIds") List<Long> roleIds);

    List<MenuDTO> findMenuTreeByUserId(@Param("userId") Long userId);

    List<SysMenuEntity> findByUserId(@Param("userId") Long userId);
}
