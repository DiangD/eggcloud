package com.qzh.eggcloud.controller;

import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.service.SysFileService;
import com.qzh.eggcloud.service.impl.FileStoreServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName FileStoreController
 * @Author DiangD
 * @Date 2021/3/12
 * @Version 1.0
 * @Description 文件夹控制器
 **/
@RestController
@Validated
@Slf4j
@RequestMapping("/folder")
public class FileFolderController {

    @Autowired
    private FileStoreServiceImpl fileStoreService;

    @Autowired
    private SysFileService fileService;

    @PostMapping("/new")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> newFileFolder(@RequestParam Long userId,
                                                            @ModelAttribute SysFile folder) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        folder.setStoreId(userDetail.getStoreId());
        folder = fileStoreService.newFileFolder(folder);
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, folder));
    }

    @GetMapping("/list")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> listUserFolders(Long userId) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        List<SysFile> folders = fileStoreService.findAllUserFolders(userDetail.getStoreId());
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, folders));
    }

    @GetMapping("/tree")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<Object> getFolderTree(@RequestParam Long userId, @RequestParam(defaultValue = "0") Long parentId) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        List<SysFile> tree = fileService.getFolderTree(userDetail.getStoreId(), parentId);
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Success, tree));
    }


}
