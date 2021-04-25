package com.qzh.eggcloud.controller;

import cn.hutool.http.HttpUtil;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.auth.dto.ShareDTO;
import com.qzh.eggcloud.service.ShareFileService;
import com.qzh.eggcloud.service.impl.SysFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FileViewController
 * @Author DiangD
 * @Date 2021/4/16
 * @Version 1.0
 * @Description 预览控制器
 **/
@RestController
public class FileViewController {
    @Autowired
    private SysFileServiceImpl sysFileService;

    @Autowired
    private ShareFileService shareFileService;

    @GetMapping("/thumbnail/{userId}/{fileId}")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<Object> ViewThumbnail(@PathVariable Long userId, @PathVariable Long fileId, String token) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        SysFile file = sysFileService.findFileOrFolder(fileId, userDetail.getStoreId());
        if (file.getIsFolder()) {
            return ResponseEntity.ok("");
        }
        try {
            byte[] content = HttpUtil.downloadBytes(file.getThumbnail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + file.getName())
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONNECTION, "close")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(content.length))
                    .header(HttpHeaders.CONTENT_ENCODING, "utf-8")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=604800")
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.ok(RespUtil.fail(null));
        }
    }

    @GetMapping("/public/thumbnail/{accessKey}/{fileId}")
    public ResponseEntity<Object> ViewPublicThumbnail(@PathVariable String accessKey, @PathVariable Long fileId) {
        ShareDTO share = shareFileService.getShareFile(accessKey);
        if (share == null) {
            return ResponseEntity.ok(RespUtil.fail(""));
        }
        SysFile file = sysFileService.findFileOrFolder(fileId, share.getStoreId());
        SysFile shareFile = sysFileService.findFileOrFolder(share.getFileId(), share.getStoreId());
        if (file.getIsFolder()) {
            return ResponseEntity.ok("");
        }
        if (!file.getId().equals(shareFile.getId())) {
            if (!file.getPath().contains(sysFileService.getUserDirectory(shareFile.getPath() + shareFile.getName()))) {
                return ResponseEntity.ok(RespUtil.fail(""));
            }
        }
        try {
            byte[] content = HttpUtil.downloadBytes(file.getThumbnail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + file.getName())
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONNECTION, "close")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(content.length))
                    .header(HttpHeaders.CONTENT_ENCODING, "utf-8")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=604800")
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.ok(RespUtil.fail(null));
        }
    }


    @GetMapping("/preview/{userId}/{fileId}/{filename}")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<Object> previewFile(@PathVariable Long userId, @PathVariable Long fileId, @PathVariable String filename, String token) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        SysFile file = sysFileService.findFileOrFolder(fileId, userDetail.getStoreId());
        if (file.getIsFolder()) {
            return ResponseEntity.ok("");
        }
        try {
            byte[] content = HttpUtil.downloadBytes(file.getUrl());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + file.getName())
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONNECTION, "close")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(content.length))
                    .header(HttpHeaders.CONTENT_ENCODING, "utf-8")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=604800")
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.ok(RespUtil.fail(null));
        }
    }

    @GetMapping("/public/preview")
    public ResponseEntity<Object> previewPublicFile(String accessKey, Long fileId, String token) {
        ShareDTO share = shareFileService.getShareFile(accessKey);
        if (share == null) {
            return ResponseEntity.ok(RespUtil.fail(""));
        }
        SysFile file = sysFileService.findFileOrFolder(fileId, share.getStoreId());
        SysFile shareFile = sysFileService.findFileOrFolder(share.getFileId(), share.getStoreId());
        if (file.getIsFolder()) {
            return ResponseEntity.ok("");
        }
        if (!file.getId().equals(shareFile.getId())) {
            if (!file.getPath().contains(sysFileService.getUserDirectory(shareFile.getPath() + shareFile.getName()))) {
                return ResponseEntity.ok(RespUtil.fail(""));
            }
        }
        try {
            byte[] content = HttpUtil.downloadBytes(file.getUrl());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + file.getName())
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONNECTION, "close")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(content.length))
                    .header(HttpHeaders.CONTENT_ENCODING, "utf-8")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=604800")
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.ok(RespUtil.fail(null));
        }
    }
}
