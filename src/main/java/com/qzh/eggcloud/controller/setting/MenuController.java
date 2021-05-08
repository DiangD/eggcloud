package com.qzh.eggcloud.controller.setting;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.model.auth.dto.MenuDTO;
import com.qzh.eggcloud.model.query.MenuQuery;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName MenuController
 * @Author DiangD
 * @Date 2021/5/5
 * @Version 1.0
 * @Description
 **/
@RestController
@RequestMapping("/menu")
@Validated
public class MenuController {
    @Autowired
    private SysMenuService menuService;

    @GetMapping("/list")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<Object> menuList(Long userId, MenuQuery query, PageEntity pageEntity) {
        PageInfo<MenuDTO> menuList = menuService.getMenuList(query, pageEntity);
        return ResponseEntity.ok(RespUtil.success(menuList));
    }

    @GetMapping("/all")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<Object> menuList(Long userId) {
        List<MenuDTO> menuList = menuService.getAllMenu();
        return ResponseEntity.ok(RespUtil.success(menuList));
    }

    @PostMapping("/add")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')}")
    public ResponseEntity<Object> addMenu(@ModelAttribute @Validated MenuDTO menu) throws BaseException {
        menuService.addMenu(menu);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/edit")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')}")
    public ResponseEntity<Object> updateMenu(@ModelAttribute @Validated MenuDTO menu) throws BaseException {
        menuService.updateMenu(menu);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')}")
    public ResponseEntity<Object> deleteMenu(@RequestParam("menuIds") Long[] menuIds) {
        menuService.deleteMenu(Arrays.asList(menuIds));
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @GetMapping("/permission")
    @PreAuthorize("{hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')}")
    public ResponseEntity<Object> permissions() {
        return ResponseEntity.ok(RespUtil.success(SecurityUtil.PERMISSIONS));
    }
}
