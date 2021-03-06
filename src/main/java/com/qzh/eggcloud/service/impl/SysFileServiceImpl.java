package com.qzh.eggcloud.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.config.CloudConfig;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.dfs.utils.EggFileUtil;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.dto.DeletedFile;
import com.qzh.eggcloud.model.dto.ShareDTO;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.service.FileStoreService;
import com.qzh.eggcloud.service.SysFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName SysFileServiceImpl
 * @Author DiangD
 * @Date 2021/3/13
 * @Version 1.0
 * @Description
 **/
@Service
@Slf4j
public class SysFileServiceImpl extends BaseService implements SysFileService {

    @Autowired
    private FileStoreService storeService;


    @Override
    public void deleteFilesOrFolders(Long storeId, List<Long> fileIds) {
        fileMapper.deleteFileByIds(fileIds, storeId);
    }

    /**
     * @param file ??????
     * @throws BaseException ???????????????
     *                       ????????????????????????
     */
    @Override
    public void updateFileOrFolder(SysFile file) throws BaseException {
        if (isExistFilenameOrFolderName(file)) {
            throw new BaseException(ErrorCode.DuplicateFolderOrFile);
        }
        file.setModifyAt(LocalDateTime.now());
        fileMapper.updateFile(file);
    }

    /**
     * @param file ??????
     * @throws BaseException ???????????????
     *                       ????????????????????????
     */
    @Override
    @Transactional
    public void addFileOrFolder(SysFile file) throws BaseException {
        if (!file.getIsFolder()) {
            if (!storeService.hasEnoughSpace(file.getStoreId(), file.getSize())) {
                throw new BaseException(ErrorCode.NoEnoughSpace);
            }
            fileMapper.insertFileOrFolder(file);
            fileStoreMapper.incrOccupy(file.getSize(), file.getStoreId());
            return;
        }
        fileMapper.insertFileOrFolder(file);
    }

    /**
     * @param query
     * @param pageEntity ??????
     * @return ??????
     */
    @Override
    public PageInfo<DeletedFile> getTrashBin(DeletedQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<DeletedFile> filesDeleted = fileMapper.findFilesFoldersDeletedByQuery(query);
        return PageInfo.of(filesDeleted);
    }

    /**
     * @param fileIds ??????Id??????
     */
    @Override
    @Transactional
    public void removeFiles(List<Long> fileIds, Long storeId) {
        for (Long id : fileIds) {
            SysFile file = fileMapper.findFileOrFolderByIdDeleted(id, storeId);
            if (file.getIsFolder()) {
                storeService.removeFileFolder(file);
            } else {
                fileMapper.removeById(file.getId(), file.getStoreId());
                fileStoreMapper.subOccupy(file.getSize(), file.getStoreId());
            }
        }
    }

    /**
     * @param fileIds ??????Id??????
     */
    @Override
    public void restoreFileOrFolder(List<Long> fileIds, Long storeId) {
        fileMapper.restoreFileOrFolderByIds(fileIds, storeId);
    }

    /**
     * @param fileSearch search model
     * @param pageEntity page
     * @return page info
     */
    @Override
    public PageInfo<SysFile> searchFiles(FileSearch fileSearch, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysFile> files = fileMapper.findFilesBySearchKey(fileSearch);
        return PageInfo.of(files);
    }

    /**
     * ????????????????????????30????????????
     */
    @Override
    public void removeFilesDeletedOver30Days() {
        List<SysFile> files = fileMapper.findFilesFoldersShouldRemove();
        List<Long> ids = Lists.newArrayList();
        for (SysFile file : files) {
            ids.add(file.getId());
        }
        fileMapper.removeByIdsNotStore(ids);
    }

    @Override
    public PageInfo<SysFile> findUserStorePage(FileQuery fileQuery, PageEntity pageEntity) {
        fileQuery.setCurrentDirectory(getUserDirectory(fileQuery.getCurrentDirectory()));
        if (StringUtils.isNotEmpty(fileQuery.getQueryFileType())) {
            fileQuery.setCurrentDirectory(null);
        }
        if (fileQuery.getFolderId() != null) {
            fileQuery.setCurrentDirectory(null);
        }

        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysFile> files = fileMapper.findByQuery(fileQuery);
        return PageInfo.of(files);
    }

    @Override
    public PageInfo<SysFile> searchOpen(FileQuery query, PageEntity pageEntity) throws BaseException {
        SysFile folder = null;
        if (query.getFolderId() != null) {
            folder = fileMapper.findById(query.getFolderId(), query.getStoreId());
        }
        if (folder == null) {
            throw new BaseException(ErrorCode.Fail);
        }
        query.setCurrentDirectory(getUserDirectory(folder.getPath() + folder.getName()));
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysFile> list = fileMapper.findByQuery(query);
        return PageInfo.of(list);
    }

    @Override
    public List<SysFile> getFolderTree(Long storeId, Long parentId) {
        String path;
        if (parentId == null || parentId == 0L) {
            path = "/";
        } else {
            SysFile folder = fileMapper.findById(parentId, storeId);
            path = getUserDirectory(folder.getPath() + folder.getName());
            return fileMapper.findFolderByPath(path, storeId);
        }
        return fileMapper.findFolderByPath(path, storeId);
    }

    @Override
    public SysFile findFileOrFolder(Long id, Long storeId) {
        return fileMapper.findById(id, storeId);
    }

    /**
     * ????????????
     */
    @Override
    @Transactional
    public void moveFiles(Long storeId, Long parentId, List<Long> fileIds) throws BaseException {
        for (Long fileId : fileIds) {
            move(storeId, parentId, fileId);
        }
    }


    private void move(Long storeId, Long to, Long from) throws BaseException {
        SysFile fromFile = fileMapper.findById(from, storeId);
        String fromPath = getRelativePathByFileId(fromFile);
        SysFile toFile = fileMapper.findById(to, storeId);
        String toPath = getRelativePathByFileId(toFile);
        if (fromFile != null) {
            SysFile moveFile = moveFileEntity(fromFile, toPath);
            if (isExistsOfToCopy(moveFile, toPath)) {
                throw new BaseException(ErrorCode.Fail.getCode(), "??????????????????????????????(???)!");
            }
            if (fromFile.getIsFolder()) {
                fileMapper.updateFile(moveFile);
                List<SysFile> fromList = fileMapper.findByPathPrefix(fromPath, storeId);
                fromList = fromList.stream().peek(file -> {
                    String oldPath = file.getPath();
                    String newPath = toPath + oldPath.substring(1);
                    moveFileEntity(file, newPath);
                }).collect(Collectors.toList());
                if (fromList.size() > 0) {
                    fileMapper.updateFiles(storeId, fromList);
                }
            } else {
                fileMapper.updateFile(moveFile);
            }
        }
    }

    private void copy(Long storeId, Long to, Long from) throws BaseException {
        SysFile fromFile = fileMapper.findById(from, storeId);
        String fromPath = getRelativePathByFileId(fromFile);
        SysFile toFile = fileMapper.findById(to, storeId);
        String toPath = getRelativePathByFileId(toFile);
        if (fromFile != null) {
            SysFile copyFile = copyFileEntity(fromFile, toPath);
            if (isExistsOfToCopy(copyFile, toPath)) {
                throw new BaseException(ErrorCode.Fail.getCode(), "??????????????????????????????(???)!");
            }
            if (fromFile.getIsFolder()) {
                addFileOrFolder(copyFile);
                List<SysFile> fromList = fileMapper.findByPathPrefix(fromPath, storeId);
                AtomicLong size = new AtomicLong(0L);
                fromList = fromList.stream().peek(file -> {
                    String oldPath = file.getPath();
                    String newPath = toPath + oldPath.substring(1);
                    copyFileEntity(file, newPath);
                    size.addAndGet(file.getSize());
                }).collect(Collectors.toList());
                if (!storeService.hasEnoughSpace(storeId, size.get())) {
                    throw new BaseException(ErrorCode.NoEnoughSpace);
                }
                if (fromList.size() > 0) {
                    fileMapper.insertFiles(fromList);
                    fileStoreMapper.incrOccupy(size.get(), storeId);
                }
            } else {
                addFileOrFolder(copyFile);
            }
        }
    }

    /**
     * @param file   ??????
     * @param toPath ????????????
     * @return ?????????????????????????????????
     */
    private boolean isExistsOfToCopy(SysFile file, String toPath) {
        int count = fileMapper.findCountNameByPath(toPath, file.getName(), file.getStoreId());
        return count >= 1;
    }


    /**
     * @param storeId ??????Id
     * @param fileIds ??????id??????
     * @throws BaseException ???????????????
     *                       ????????????
     */
    @Transactional
    public void copyFiles(Long storeId, Long parentId, List<Long> fileIds) throws BaseException {
        for (Long fileId : fileIds) {
            copy(storeId, parentId, fileId);
        }
    }

    /**
     * @param file ??????
     * @return ?????????????????????????????????????????????
     */
    public Boolean isExistFilenameOrFolderName(SysFile file) {
        return fileMapper.findCountNameByPath(file.getPath(), file.getName(), file.getStoreId()) > 0;
    }

    /***
     * ??????????????????(?????????)
     * @param currentDirectory ????????????
     * @return ????????????
     */
    public String getUserDirectory(String currentDirectory) {
        if (StringUtils.isEmpty(currentDirectory)) {
            currentDirectory = CloudConfig.separator;
        } else {
            if (!currentDirectory.endsWith(CloudConfig.separator)) {
                currentDirectory += CloudConfig.separator;
            }
        }
        currentDirectory = currentDirectory.replaceAll("/" + CloudConfig.separator, File.separator);
        return currentDirectory;
    }

    /***
     * ????????????Id???????????????????????????
     * @param fileDocument ??????
     * @return ????????????
     */
    public String getRelativePathByFileId(SysFile fileDocument) {
        if (fileDocument == null) {
            return getUserDirectory(null);
        }
        if (fileDocument.getIsFolder()) {
            return getUserDirectory(fileDocument.getPath() + fileDocument.getName());
        }
        String currentDirectory = fileDocument.getPath() + fileDocument.getName();
        return currentDirectory.replaceAll("/" + CloudConfig.separator, File.separator);
    }

    /**
     * @param file ??????
     * @param to   ????????????
     * @return ????????????
     */
    private SysFile copyFileEntity(SysFile file, String to) {
        file.setId(null);
        file.setModifyAt(LocalDateTime.now());
        file.setPath(to);
        return file;
    }

    /**
     * @param file ??????
     * @param to   ????????????
     * @return ????????????
     */
    private SysFile moveFileEntity(SysFile file, String to) {
        file.setModifyAt(LocalDateTime.now());
        file.setPath(to);
        return file;
    }

    /**
     * @param request  http request
     * @param response http response
     * @param fileIds  ??????Id??????
     *                 ??????????????????
     */
    @Override
    public void packageDownload(HttpServletRequest request, HttpServletResponse response, List<Long> fileIds) throws IOException, BaseException {
        //??????????????????
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        //????????????????????????
        setDownloadName(request, response, Instant.now().toEpochMilli() + ".zip");

        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        List<SysFile> files = Lists.newArrayList();
        long totalSize = 0;
        for (Long fileId : fileIds) {
            SysFile sysFile = fileMapper.findById(fileId, userDetail.getStoreId());
            totalSize += sysFile.getSize();
            if (sysFile.getIsFolder()) {
                List<SysFile> fileList = fileMapper.findByPathPrefix(getUserDirectory(sysFile.getPath() + sysFile.getName()), userDetail.getStoreId());
                for (SysFile file : fileList) {
                    totalSize += file.getSize();
                    if (!file.getIsFolder()) {
                        files.add(file);
                    }
                }
            } else {
                files.add(sysFile);
            }
        }
        if (totalSize <= 0) {
            throw new BaseException(ErrorCode.EmptyFile);
        }

        Map<String, InputStream> fileMap = Maps.newHashMap();
        files.forEach(file -> {
            String filePath = file.getPath() + file.getName();
            InputStream in = EggFileUtil.downloadToInputStream(file.getGroup(), file.getRemotePath());
            fileMap.put(filePath, in);
        });

        ArrayList<String> paths = Lists.newArrayList(fileMap.keySet());
        ArrayList<InputStream> inputStreams = Lists.newArrayList(fileMap.values());

        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        ZipUtil.zip(zipOutputStream, paths.toArray(new String[0]), inputStreams.toArray(new InputStream[0]));
    }

    @Override
    public void publicPackageDownload(HttpServletRequest request, HttpServletResponse response, List<Long> fileIds) throws IOException, BaseException {
        //??????????????????
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        //????????????????????????
        setDownloadName(request, response, Instant.now().toEpochMilli() + ".zip");

        String accessKey = request.getParameter("accessKey");
        ShareDTO shareDTO = shareFileMapper.findShareFileByAccessKey(accessKey);
        List<SysFile> files = Lists.newArrayList();
        long totalSize = 0;
        for (Long fileId : fileIds) {
            SysFile sysFile = fileMapper.findById(fileId, shareDTO.getStoreId());
            totalSize += sysFile.getSize();
            if (sysFile.getIsFolder()) {
                List<SysFile> fileList = fileMapper.findByPathPrefix(getUserDirectory(sysFile.getPath() + sysFile.getName()), shareDTO.getStoreId());
                for (SysFile file : fileList) {
                    totalSize += file.getSize();
                    if (!file.getIsFolder()) {
                        files.add(file);
                    }
                }
            } else {
                files.add(sysFile);
            }
        }
        if (totalSize <= 0) {
            throw new BaseException(ErrorCode.EmptyFile);
        }

        Map<String, InputStream> fileMap = Maps.newHashMap();
        files.forEach(file -> {
            String filePath = file.getPath() + file.getName();
            InputStream in = EggFileUtil.downloadToInputStream(file.getGroup(), file.getRemotePath());
            fileMap.put(filePath, in);
        });

        ArrayList<String> paths = Lists.newArrayList(fileMap.keySet());
        ArrayList<InputStream> inputStreams = Lists.newArrayList(fileMap.values());

        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        ZipUtil.zip(zipOutputStream, paths.toArray(new String[0]), inputStreams.toArray(new InputStream[0]));
    }

    @Override
    public SysFile getShareFile(Long id) {
        return fileMapper.findShareFileById(id);
    }

    private void setDownloadName(HttpServletRequest request, HttpServletResponse response, String downloadName) {
        try {
            //?????????????????????IE/Chrome/firefox???????????????????????????????????????Trident(IE)???Gecko(Firefox??????)???WebKit(Safari??????,Chrome????????????,??????)??????Presto(Opera?????????) (?????????)
            String gecko = "Gecko", webKit = "WebKit";
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains(gecko) || userAgent.contains(webKit)) {
                downloadName = new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            } else {
                downloadName = URLEncoder.encode(downloadName, "UTF-8");
            }
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + downloadName + "\"");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
