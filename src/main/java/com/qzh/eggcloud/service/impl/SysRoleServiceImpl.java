package com.qzh.eggcloud.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.mapper.SysRoleMapper;
import com.qzh.eggcloud.model.auth.SysRoleEntity;
import com.qzh.eggcloud.model.auth.dto.RoleDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.query.RoleQuery;
import com.qzh.eggcloud.service.SysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SysRoleServiceImpl
 * @Author DiangD
 * @Date 2021/5/5
 * @Version 1.0
 * @Description
 **/
@Service
public class SysRoleServiceImpl extends BaseService implements SysRoleService {
    @Override
    public PageInfo<RoleDTO> listRoles(RoleQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<RoleDTO> list = sysRoleMapper.findByQuery(query);
        return PageInfo.of(list);
    }

    @Override
    public void addRole(RoleDTO roleDTO) throws BaseException {
        if (isExistRoleName(roleDTO.getRoleName())) {
            throw new BaseException(ErrorCode.Fail.getCode(), "该角色标识已存在");
        }
        SysRoleEntity roleDO = new SysRoleEntity();
        BeanUtils.copyProperties(roleDTO, roleDO);
        roleDO.setCreateAt(LocalDateTime.now());
        roleDO.setUpdateAt(LocalDateTime.now());
        sysRoleMapper.insertRole(roleDO);
    }

    @Override
    @Transactional
    public void updateRole(RoleDTO roleDTO) throws BaseException {
        SysRoleEntity byName = sysRoleMapper.findByName(roleDTO.getRoleName());
        if (byName != null) {
            if (!byName.getRoleId().equals(roleDTO.getRoleId())) {
                throw new BaseException(ErrorCode.Fail.getCode(), "该角色标识已存在");
            }
        }
        SysRoleEntity roleDO = new SysRoleEntity();
        BeanUtils.copyProperties(roleDTO, roleDO);
        saveRoleMenuIds(roleDO);
        roleDO.setUpdateAt(LocalDateTime.now());
        sysRoleMapper.updateRole(roleDO);
    }

    @Override
    @Transactional
    public void deleteRole(List<Long> roleIds) {
        sysRoleMapper.deleteByIds(roleIds);
        sysRoleMapper.deleteRoleMenuByRoleIds(roleIds);
    }

    @Transactional
    public void saveRoleMenuIds(SysRoleEntity roleDO) {
        if (roleDO.getMenuIds() == null) {
            return;
        }
        if (roleDO.getMenuIds().size() == 0) {
            sysRoleMapper.deleteById(roleDO.getRoleId());
        } else {
            sysRoleMapper.deleteById(roleDO.getRoleId());
            sysRoleMapper.insertRoleMenu(roleDO);
        }
    }

    private boolean isExistRoleName(String roleName) {
        return sysRoleMapper.findCountByName(roleName) > 0;
    }
}
