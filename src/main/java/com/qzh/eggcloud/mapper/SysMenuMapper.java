package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.auth.SysMenuEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.MenuQuery;
import com.qzh.eggcloud.service.SysMenuService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysMenuMapper {
    List<MenuDTO> findMenuTreeByRoleIds(@Param("roleIds") List<Long> roleIds);

    List<MenuDTO> findMenuTreeByUserId(@Param("userId") Long userId);

    List<SysMenuEntity> findByUserId(@Param("userId") Long userId);

    List<MenuDTO> findMenuTreeByQuery(@Param("query") MenuQuery query);

    int findCountByName(@Param("name") String name);

    SysMenuEntity findById(@Param("id") Long id);

    int insertMenu(@Param("menu") SysMenuEntity menu);

    int updateMenu(@Param("menu") SysMenuEntity menu);

    SysMenuEntity findByName(String name);

    List<SysMenuEntity> findByIds(@Param("ids") List<Long> ids);

    List<SysMenuEntity> findByParentIds(@Param("ids") List<Long> ids);

    int deleteByIds(@Param("ids") List<Long> ids);
}
