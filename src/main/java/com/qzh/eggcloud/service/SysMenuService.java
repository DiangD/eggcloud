package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.MenuQuery;
import com.qzh.eggcloud.model.query.PageEntity;

import java.util.List;

public interface SysMenuService {
    PageInfo<MenuDTO> getMenuList(MenuQuery query, PageEntity pageEntity);

    void addMenu(MenuDTO menu) throws BaseException;

    void updateMenu(MenuDTO menu) throws BaseException;

    void deleteMenu(List<Long> menuIds);

    List<MenuDTO> getAllMenu();

}
