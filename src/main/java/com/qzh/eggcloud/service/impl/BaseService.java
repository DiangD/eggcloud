package com.qzh.eggcloud.service.impl;

import com.qzh.eggcloud.mapper.*;

import javax.annotation.Resource;

/**
 * @ClassName BaseService
 * @Author DiangD
 * @Date 2021/3/14
 * @Version 1.0
 * @Description
 **/
public class BaseService {
    @Resource
    protected SysUserMapper sysUserMapper;

    @Resource
    protected SysRoleMapper sysRoleMapper;

    @Resource
    protected FileStoreMapper fileStoreMapper;

    @Resource
    protected FileFolderMapper fileFolderMapper;

    @Resource
    protected FileMapper fileMapper;

    @Resource
    protected ShareFileMapper shareFileMapper;

    @Resource
    protected SysTaskMapper sysTaskMapper;

    @Resource
    protected SysMenuMapper sysMenuMapper;

}
