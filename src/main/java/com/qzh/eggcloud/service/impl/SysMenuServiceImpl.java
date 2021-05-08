package com.qzh.eggcloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.mapper.SysRoleMapper;
import com.qzh.eggcloud.model.auth.SysMenuEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.MenuQuery;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.service.SysMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName SysMenuServiceImpl
 * @Author DiangD
 * @Date 2021/5/5
 * @Version 1.0
 * @Description
 **/
@Service
public class SysMenuServiceImpl extends BaseService implements SysMenuService {
    @Override
    public PageInfo<MenuDTO> getMenuList(MenuQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<MenuDTO> tree = sysMenuMapper.findMenuTreeByQuery(query);
        return PageInfo.of(tree);
    }

    @Override
    public void addMenu(MenuDTO menu) throws BaseException {
        if (isExistMenuName(menu.getName())) {
            throw new BaseException(ErrorCode.Fail.getCode(), "该菜单名称已存在");
        }
        if (menu.getParentId() != null) {
            SysMenuEntity parent = sysMenuMapper.findById(menu.getParentId());
            if (parent == null) {
                throw new BaseException(ErrorCode.Fail.getCode(), "该父分级菜单不存在");
            }
        }
        SysMenuEntity menuDO = new SysMenuEntity();
        BeanUtils.copyProperties(menu, menuDO);
        menuDO.setMenuId(null);
        menuDO.setCreateAt(LocalDateTime.now());
        menuDO.setUpdateAt(LocalDateTime.now());
        sysMenuMapper.insertMenu(menuDO);
    }

    @Override
    public void updateMenu(MenuDTO menu) throws BaseException {
        SysMenuEntity byName = sysMenuMapper.findByName(menu.getName());
        if (byName != null) {
            if (!byName.getMenuId().equals(menu.getMenuId())) {
                throw new BaseException(ErrorCode.Fail.getCode(), "该菜单名称已存在");
            }
        }
        if (menu.getParentId() != null) {
            SysMenuEntity parent = sysMenuMapper.findById(menu.getParentId());
            if (parent == null) {
                throw new BaseException(ErrorCode.Fail.getCode(), "该父分级菜单不存在");
            }
        }
        SysMenuEntity menuDO = new SysMenuEntity();
        BeanUtils.copyProperties(menu, menuDO);
        menuDO.setUpdateAt(LocalDateTime.now());
        sysMenuMapper.updateMenu(menuDO);
    }

    @Override
    @Transactional
    public void deleteMenu(List<Long> menuIds) {
        List<Long> ids = findLoopMenuIds(false, menuIds);
        sysMenuMapper.deleteByIds(ids);
        sysRoleMapper.deleteByMenuIds(ids);
    }

    @Override
    public List<MenuDTO> getAllMenu() {
        MenuQuery query = new MenuQuery();
        query.setSortProp("weight");
        query.setSortOrder("desc");
        return sysMenuMapper.findMenuTreeByQuery(query);
    }

    private List<Long> findLoopMenuIds(boolean isParent, List<Long> menuIdList) {
        final List<Long> menuIds = Lists.newArrayList();
        List<SysMenuEntity> res;
        if (!isParent) {
            res = sysMenuMapper.findByIds(menuIdList);
        } else {
            res = sysMenuMapper.findByParentIds(menuIdList);
            menuIds.addAll(menuIdList);
        }
        List<Long> resIds = res.stream().map(SysMenuEntity::getMenuId).collect(Collectors.toList());
        if (resIds.size() > 0) {
            menuIds.addAll(findLoopMenuIds(true, resIds));
        }
        return menuIds;
    }

    private boolean isExistMenuName(String name) {
        return sysMenuMapper.findCountByName(name) > 0;
    }
}
