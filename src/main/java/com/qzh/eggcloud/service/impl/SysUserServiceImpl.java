package com.qzh.eggcloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.qzh.eggcloud.common.SystemConst;
import com.qzh.eggcloud.common.config.CloudConfig;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.model.FileStore;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.auth.SysMenuEntity;
import com.qzh.eggcloud.model.auth.SysRoleEntity;
import com.qzh.eggcloud.model.auth.SysUserEntity;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.UserQuery;
import com.qzh.eggcloud.service.FileStoreService;
import com.qzh.eggcloud.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName SysUserServiceImpl
 * @Author DiangD
 * @Date 2021/3/5
 * @Version 1.0
 * @Description user service
 **/
@Service(value = "SysUserServiceImpl")
public class SysUserServiceImpl extends BaseService implements SysUserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FileStoreService fileStoreService;

    /**
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public SysUserEntity findByUsername(String username) {
        return sysUserMapper.findByUsername(username);
    }

    /**
     * @param email 邮箱
     * @return 用户实体
     */
    @Override
    public SysUserEntity findByEmail(String email) {
        return sysUserMapper.findByEmail(email);
    }

    /**
     * @param username 用户名
     * @return 是否存在相同用户名
     */
    @Override
    public Boolean isExistUsername(String username) {
        return this.findByUsername(username) != null;
    }

    /**
     * @param email 邮箱
     * @return 是否存在相同邮箱
     */
    @Override
    public Boolean isExistEmail(String email) {
        return this.findByEmail(email) != null;
    }

    /**
     * @param userId 用户id
     * @return 角色实体
     */
    @Override
    public List<SysRoleEntity> selectRoleByUserId(Long userId) {
        return sysUserMapper.selectSysRoleByUserId(userId);
    }

    /**
     * @param user 用户实体
     * @return 更新条数
     */
    @Override
    public int saveOrUpdateUser(SysUserEntity user) {
        if (user.getUserId() != null) {
            return sysUserMapper.updateUser(user);
        }
        return sysUserMapper.insertUser(user);
    }

    /**
     * @param user 用户实体
     * @return 是否插入成功
     */
    @Override
    @Transactional
    public Boolean registerUser(SysUserEntity user) {
        SysRoleEntity role = sysRoleMapper.findRoleByName("USER");
        if (role == null) {
            return false;
        }
        FileStore fileStore = FileStore.builder()
                .occupy(0L)
                .size(CloudConfig.maxSize)
                .build();
        fileStoreService.addFileStore(fileStore);
        user.setStoreId(fileStore.getStoreId());
        sysUserMapper.insertUser(user);
        sysUserMapper.insertUserRole(user.getUserId(), role.getRoleId());
        return true;
    }

    /**
     * @param userId 用户id
     * @param avatar 头像链接
     */
    @Override
    public void setUserAvatar(Long userId, String avatar) {
        sysUserMapper.updateAvatar(userId, avatar);
    }

    /**
     * @param userId 用户id
     * @return 用户实体
     */
    @Override
    public SysUserEntity findByUserId(Long userId) {
        return sysUserMapper.findByUserId(userId);
    }

    /**
     * @param user 用户实体
     * @return 数据库更新条数
     * @throws BaseException 统一异常
     */
    @Override
    @Transactional
    public int updateUserInfo(SysUserEntity user) throws BaseException {
        SysUserEntity userByName = findByUsername(user.getUsername());
        if (userByName != null && !userByName.getUserId().equals(user.getUserId())) {
            throw new BaseException(ErrorCode.Fail.getCode(), "用户名已存在");
        }
        return sysUserMapper.updateUser(user);
    }

    /**
     * @param query      query model
     * @param pageEntity page
     * @return 用户详情包括角色分页
     */
    @Override
    public PageInfo<SysUserEntity> findUsersWithDetail(UserQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysUserEntity> users = sysUserMapper.findUserDetailByUser(query);
        return PageInfo.of(users);
    }

    @Override
    public List<SysUserEntity> findUserDetails(UserQuery userQuery) {
        return sysUserMapper.findUserDetailByUser(userQuery);
    }

    /**
     * @param userId 用户id
     *               删除用户
     */
    @Override
    @Transactional
    public void removeUser(Long userId) {
        if (userId == null) {
            return;
        }
        sysUserMapper.deleteUserById(userId);
        sysUserMapper.deleteSysRoleUserByUserId(userId);
    }

    /**
     * @param userId 用户id
     *               改变用户状态 active lock
     */
    @Override
    public void changeUserStatus(Long userId) {
        SysUserEntity user = sysUserMapper.findByUserId(userId);
        if (user == null) {
            return;
        }
        int status = user.getStatus() == 1 ? 0 : 1;
        user.setStatus(status);
        sysUserMapper.updateUser(user);
    }

    /**
     * @param userId 用户id
     * @return 用户菜单树
     * 获取用户菜单树
     */
    @Override
    public List<MenuDTO> getUserMenuTree(Long userId) {
        String key = String.format(SystemConst.USER_MENU_TREE, userId);
        @SuppressWarnings("unchecked")
        List<MenuDTO> menuTree = (List<MenuDTO>) redisTemplate.opsForValue().get(key);
        if (menuTree == null) {
            menuTree = sysMenuMapper.findMenuTreeByUserId(userId);
            redisTemplate.opsForValue().set(key, menuTree, 10, TimeUnit.MINUTES);
        }
        return menuTree;
    }

    /**
     * @param userId 用户id
     * @return 菜单列表
     * 获取用户菜单列表
     */
    @Override
    public List<SysMenuEntity> getUserMenus(Long userId) {
        return sysMenuMapper.findByUserId(userId);
    }

    public Set<String> getUserPermissions(Long userId) {
        List<MenuDTO> menuTree = getUserMenuTree(userId);
        if (menuTree == null) {
            return null;
        }
        HashSet<String> permissions = Sets.newHashSet();
        menuTree.forEach(item -> {
            travelMenuTree(item, permissions);
        });
        return permissions;
    }


    /**
     * @param menu        菜单树
     * @param permissions 权限列表
     *                    递归权限树，获取权限列表
     */
    public void travelMenuTree(MenuDTO menu, Set<String> permissions) {
        if (menu == null) {
            return;
        }
        if (StringUtils.isNotEmpty(menu.getPermission())) {
            permissions.add(menu.getPermission());
        }
        if (menu.getChildren() != null && menu.getChildren().size() > 0) {
            menu.getChildren().forEach(child -> {
                travelMenuTree(child, permissions);
            });
        }
    }
}
