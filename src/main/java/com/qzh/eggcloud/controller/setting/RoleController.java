package com.qzh.eggcloud.controller.setting;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.model.auth.dto.RoleDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.query.RoleQuery;
import com.qzh.eggcloud.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @ClassName RoleController
 * @Author DiangD
 * @Date 2021/5/5
 * @Version 1.0
 * @Description
 **/
@RestController
@RequestMapping("/role")
@Validated
public class RoleController {

    @Autowired
    private SysRoleService roleService;

    @GetMapping("/list")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')}")
    public ResponseEntity<Object> roleList(RoleQuery query, PageEntity pageEntity) {
        PageInfo<RoleDTO> roles = roleService.listRoles(query, pageEntity);
        return ResponseEntity.ok(RespUtil.success(roles));
    }

    @PostMapping("/add")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')}")
    public ResponseEntity<Object> addRole(@ModelAttribute @Validated RoleDTO role) throws BaseException {
        roleService.addRole(role);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/edit")
    @PreAuthorize("{hasPermission('/role/edit','admin:role:edit')}")
    public ResponseEntity<Object> updateRole(@ModelAttribute @Validated RoleDTO role) throws BaseException {
        roleService.updateRole(role);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("{hasPermission('/role/delete','admin:role:delete')}")
    public ResponseEntity<Object> deleteRole(@RequestParam("roleIds") Long[] roleIds) {
        roleService.deleteRole(Arrays.asList(roleIds));
        return ResponseEntity.ok(RespUtil.success(null));
    }
}
