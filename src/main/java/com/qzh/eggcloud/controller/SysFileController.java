package com.qzh.eggcloud.controller;

import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.SystemConst;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.FileContentTypeUtil;
import com.qzh.eggcloud.common.utils.RespUtil;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.dfs.EggCom;
import com.qzh.eggcloud.dfs.model.EggFileInfo;
import com.qzh.eggcloud.dfs.utils.EggFileUtil;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.dto.DeletedFile;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.service.FileStoreService;
import com.qzh.eggcloud.service.impl.SysFileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SysFileController
 * @Author DiangD
 * @Date 2021/3/13
 * @Version 1.0
 * @Description 文件控制器
 **/
@RestController
@RequestMapping("/ff")
@Slf4j
public class SysFileController {

    @Autowired
    private SysFileServiceImpl sysFileService;

    @Autowired
    private FileStoreService fileStoreService;

    @DeleteMapping("/delete")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> deleteFileOrFolder(@RequestParam Long userId, @RequestParam("fileIds") Long[] fileIds) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        sysFileService.deleteFilesOrFolders(userDetail.getStoreId(), Arrays.asList(fileIds));
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/move")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> moveFile(@RequestParam Long userId, @RequestParam(defaultValue = "0") Long parentId, Long[] fileIds) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        sysFileService.moveFiles(userDetail.getStoreId(), parentId, Arrays.asList(fileIds));
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/copy")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> copyFile(@RequestParam Long userId, @RequestParam(defaultValue = "0") Long parentId, Long[] fileIds) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        sysFileService.copyFiles(userDetail.getStoreId(), parentId, Arrays.asList(fileIds));
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/rename")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> renameFile(@RequestParam Long userId, @ModelAttribute SysFile file) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        file.setStoreId(userDetail.getStoreId());
        sysFileService.updateFileOrFolder(file);
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/upload")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> upload(@RequestParam("file") MultipartFile file, Long userId,
                                                     @RequestParam(defaultValue = "/") String currentDirectory,
                                                     @RequestHeader HttpHeaders headers) throws IOException, BaseException {

        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        if (!fileStoreService.hasEnoughSpace(userDetail.getStoreId(), file.getSize())) {
            throw new BaseException(ErrorCode.NoEnoughSpace);
        }

        String filename;

        if (sysFileService.isExistFilenameOrFolderName(
                SysFile.builder().storeId(userDetail.getStoreId())
                        .name(file.getOriginalFilename())
                        .isFolder(false)
                        .build())) {
            filename = EggFileUtil.generateFileName(file.getOriginalFilename());
        } else {
            filename = file.getOriginalFilename();
        }

        Map<String, String> header = null;
        String headerMD5 = headers.getFirst(SystemConst.HEADER_EGGCLOUD_FILE_HASH);
        if (StringUtils.isNotEmpty(headerMD5)) {
            header = Maps.newHashMap();
            header.put(EggCom.HEADER_FILE_HASH, headerMD5);
        }

        assert filename != null;
        JsonResult<EggFileInfo> result = EggFileUtil.uploadFile(file.getInputStream(), file.getOriginalFilename(), header);
        if (result.getStatus() == ErrorCode.FileUploadSuccess.getCode()) {
            String contentType = FileContentTypeUtil.getContentType(FileUtil.extName(filename));
            String thumbnail = null;
            if (contentType.contains("image")) {
                JsonResult<EggFileInfo> imageRes = uploadImage(file);
                if (imageRes.getStatus() == ErrorCode.FileUploadSuccess.getCode()) {
                    log.info(String.valueOf(imageRes));
                    thumbnail = imageRes.getData().getUrl();
                }
            }

            EggFileInfo fileInfo = result.getData();
            SysFile sysFile = SysFile.builder()
                    .size(fileInfo.getSize())
                    .name(filename)
                    .group(fileInfo.getGroup())
                    .url(fileInfo.getUrl())
                    .remotePath(fileInfo.getPath())
                    .isFolder(false)
                    .md5(fileInfo.getMd5())
                    .storeId(userDetail.getStoreId())
                    .contentType(contentType)
                    .extension(FileUtil.extName(file.getOriginalFilename()))
                    .thumbnail(thumbnail)
                    .path(sysFileService.getUserDirectory(currentDirectory))
                    .modifyAt(LocalDateTime.now())
                    .createAt(LocalDateTime.now())
                    .build();
            sysFileService.addFileOrFolder(sysFile);
            return ResponseEntity.ok(RespUtil.uploadSuccess(null));
        }
        return ResponseEntity.ok(RespUtil.uploadFail(null));
    }


    public JsonResult<EggFileInfo> uploadImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        BufferedImage bufferedImage = Thumbnails.of(image).size(200, 200).asBufferedImage();
        InputStream in = bufferedImageToInputStream(bufferedImage);
        String filename = file.getName() + "-thumbnail." + FileUtil.extName(file.getOriginalFilename());
        return EggFileUtil.uploadFile(in, filename, null);
    }

    /**
     * 将BufferedImage转换为InputStream
     *
     * @param image 图片
     * @return 输入流
     */
    public InputStream bufferedImageToInputStream(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            log.error("提示:", e);
        }
        return null;
    }

    @GetMapping("/trash/list")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> filesTrashBin(@RequestParam Long userId, DeletedQuery query,
                                                            PageEntity pageEntity) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        query.setStoreId(userDetail.getStoreId());
        PageInfo<DeletedFile> trashBin = sysFileService.getTrashBin(query, pageEntity);
        return ResponseEntity.ok(RespUtil.success(trashBin));
    }

    @DeleteMapping("/remove")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> removeFileOrFolder(@RequestParam Long userId, Long[] fileIds) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        sysFileService.removeFiles(Arrays.asList(fileIds), userDetail.getStoreId());
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @PostMapping("/restore")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> restoreFileOrFolder(@RequestParam Long userId, Long[] fileIds) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        sysFileService.restoreFileOrFolder(Arrays.asList(fileIds), userDetail.getStoreId());
        return ResponseEntity.ok(RespUtil.success(null));
    }

    @GetMapping("/search")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<JsonResult<Object>> searchFiles(@RequestParam Long userId,
                                                          FileSearch fileSearch,
                                                          PageEntity pageEntity) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        fileSearch.setStoreId(userDetail.getStoreId());
        PageInfo<SysFile> page = sysFileService.searchFiles(fileSearch, pageEntity);
        return ResponseEntity.ok(RespUtil.success(page));
    }

    @GetMapping("/page")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#fileQuery.userId)}")
    public ResponseEntity<JsonResult<Object>> getUserStorePage(FileQuery fileQuery, PageEntity pageEntity) {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        fileQuery.setStoreId(userDetail.getStoreId());
        PageInfo<SysFile> page = sysFileService.findUserStorePage(fileQuery, pageEntity);
        return ResponseEntity.ok(RespUtil.success(page));
    }

    @GetMapping("/download/{userId}/{fileId}")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public ResponseEntity<Object> downFile(@PathVariable Long userId, @PathVariable Long fileId, String token, HttpServletResponse response) throws IOException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        SysFile sysFile = sysFileService.findFileOrFolder(fileId, userDetail.getStoreId());
        if (sysFile == null) {
            return ResponseEntity.ok(RespUtil.fail(null));
        }
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setHeader(HttpHeaders.CONNECTION, "close");
        response.setHeader("Content-Disposition", "attachment; filename=" + sysFile.getName());
        response.setContentType(sysFile.getContentType());
        Map<String, String> params = Maps.newHashMap();
        params.put("group", sysFile.getGroup());
        params.put("file", sysFile.getRemotePath());
        EggFileUtil.downloadFile(params, response.getOutputStream());
        return null;
    }

    @GetMapping("/search/open")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#query.userId)}")
    public ResponseEntity<Object> searchOpen(FileQuery query, PageEntity pageEntity) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        query.setStoreId(userDetail.getStoreId());
        PageInfo<SysFile> pageInfo = sysFileService.searchOpen(query, pageEntity);
        return ResponseEntity.ok(RespUtil.success(pageInfo));
    }

    @GetMapping("/packageDownload")
    @PreAuthorize("{hasAnyRole('ROLE_USER','ROLE_ADMIN')&&@SecurityUtil.isLoginUser(#userId)}")
    public void packageDownload(HttpServletRequest request, HttpServletResponse response, Long userId, Long[] fileIds, String token) throws BaseException, IOException {
        if (fileIds != null && fileIds.length > 0) {
            List<Long> fileIdList = Arrays.asList(fileIds);
            sysFileService.packageDownload(request, response, fileIdList);
        } else {
            throw new BaseException(ErrorCode.Fail);
        }
    }
}
