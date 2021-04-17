package com.qzh.eggcloud.controller;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.FileContentTypeUtil;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.dfs.utils.EggFileUtil;
import com.qzh.eggcloud.model.auth.dto.ShareDTO;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.ShareFile;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.query.ShareQuery;
import com.qzh.eggcloud.service.SysFileService;
import com.qzh.eggcloud.service.impl.ShareFileServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @ClassName FileShareController
 * @Author DiangD
 * @Date 2021/3/15
 * @Version 1.0
 * @Description 文件分享控制器
 **/
@RestController
@RequestMapping("/s")
@Validated
public class FileShareController {

    @Autowired
    private ShareFileServiceImpl shareFileService;

    @Autowired
    private SysFileService sysFileService;

    @PostMapping("/create")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> createShareFile(@RequestParam Long userId, @Validated @ModelAttribute ShareFile shareFile,
                                                              @RequestParam("mode") Integer durationMode) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        shareFile.setStoreId(userDetail.getStoreId());
        shareFile.setCreateAt(LocalDateTime.now());
        ShareFile file = setExpireTime(shareFile, durationMode);
        file = shareFileService.generateShareFile(file);
        return ResponseEntity.ok(RespUtil.success(file));
    }

    @GetMapping("/public/{accessKey}")
    public ResponseEntity<JsonResult<Object>> accessShareFile(@PathVariable String accessKey, String code) {
        ShareDTO file = shareFileService.getShareFile(accessKey);
        if (file == null) {
            return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), "无效的分享", null));
        }
        if (file.getHasVerify()) {
            if (StringUtils.isEmpty(code)) {
                return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), "请输入提取码", null));
            }
            if (file.getCode().equals(code)) {
                return ResponseEntity.ok(RespUtil.success(file));
            } else {
                return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), "提取码错误", null));
            }
        }
        return ResponseEntity.ok(RespUtil.fail(null));
    }

    @DeleteMapping("/cancel")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> cancelShareFile(@RequestParam Long userId, @RequestParam("accessKeys") String[] accessKeys) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        shareFileService.cancelShares(accessKeys, userDetail.getStoreId());
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @GetMapping("/list")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> listShareFiles(@RequestParam Long userId, ShareQuery shareQuery, @ModelAttribute PageEntity pageEntity) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        shareQuery.setStoreId(userDetail.getStoreId());
        PageInfo<ShareDTO> pageInfo = shareFileService.getUserShareList(shareQuery, pageEntity);
        return ResponseEntity.ok(RespUtil.success(pageInfo));
    }

    @GetMapping("/public/download/{accessKey}")
    public ResponseEntity<Object> downloadShareFile(@PathVariable String accessKey, HttpServletResponse response) throws IOException {
        ShareDTO shareFile = shareFileService.getShareFile(accessKey);
        if (shareFile == null) {
            return ResponseEntity.ok(RespUtil.fail(null));
        }
        SysFile sysFile = sysFileService.findFileOrFolder(shareFile.getFileId(), shareFile.getStoreId());
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setHeader(HttpHeaders.CONNECTION, "close");
        response.setHeader("Content-Disposition", "attachment; filename=" + sysFile.getName());
        response.setContentType(FileContentTypeUtil.getContentType(sysFile.getExtension()));
        EggFileUtil.downloadFile(sysFile.getGroup(), sysFile.getPath(), response.getOutputStream());
        return null;
    }

    private ShareFile setExpireTime(ShareFile file, Integer mode) {
        switch (mode) {
            case 1: {
                file.setExpireAt(LocalDateTime.now().plusDays(7));
                return file;
            }
            case 2: {
                file.setExpireAt(LocalDateTime.now().plusDays(1));
                return file;
            }
            default: {
                file.setExpireAt(null);
                return file;
            }
        }
    }
}
