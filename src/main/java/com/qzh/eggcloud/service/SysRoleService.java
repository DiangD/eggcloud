package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.auth.dto.RoleDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.query.RoleQuery;

import java.util.List;

public interface SysRoleService {
    PageInfo<RoleDTO> listRoles(RoleQuery query, PageEntity pageEntity);

    void addRole(RoleDTO roleDTO) throws BaseException;

    void updateRole(RoleDTO roleDTO) throws BaseException;

    void deleteRole(List<Long> roleIds);
}
