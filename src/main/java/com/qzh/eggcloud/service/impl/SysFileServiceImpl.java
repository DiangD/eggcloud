package com.qzh.eggcloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.qzh.eggcloud.common.config.CloudConfig;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.auth.dto.DeletedFile;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.service.FileStoreService;
import com.qzh.eggcloud.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @ClassName SysFileServiceImpl
 * @Author DiangD
 * @Date 2021/3/13
 * @Version 1.0
 * @Description
 **/
@Service
public class SysFileServiceImpl extends BaseService implements SysFileService {

    @Autowired
    private FileStoreService storeService;


    @Override
    public void deleteFilesOrFolders(Long storeId, List<Long> fileIds) {
        fileMapper.deleteFileByIds(fileIds, storeId);
    }

    /**
     * @param file 文件
     * @throws BaseException 自定义异常
     *                       更新文件或文件夹
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
     * @param file 文件
     * @throws BaseException 自定义异常
     *                       新增文件或文件夹
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
     * @param pageEntity 页数
     * @return 分页
     */
    @Override
    public PageInfo<DeletedFile> getTrashBin(DeletedQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<DeletedFile> filesDeleted = fileMapper.findFilesFoldersDeletedByQuery(query);
        return PageInfo.of(filesDeleted);
    }

    /**
     * @param fileIds 文件Id列表
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
     * @param fileIds 文件Id列表
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
     * 删除回收站内超过30天的文件
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
     * 移动文件
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
                throw new BaseException(ErrorCode.Fail.getCode(), "所选目录已存在该文件(夹)!");
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
                throw new BaseException(ErrorCode.Fail.getCode(), "所选目录已存在该文件(夹)!");
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
     * @param file   文件
     * @param toPath 目标路径
     * @return 该路径是否存在同名文件
     */
    private boolean isExistsOfToCopy(SysFile file, String toPath) {
        int count = fileMapper.findCountNameByPath(toPath, file.getName(), file.getStoreId());
        return count >= 1;
    }


    /**
     * @param storeId 仓库Id
     * @param fileIds 文件id列表
     * @throws BaseException 自定义异常
     *                       复制文件
     */
    @Transactional
    public void copyFiles(Long storeId, Long parentId, List<Long> fileIds) throws BaseException {
        for (Long fileId : fileIds) {
            copy(storeId, parentId, fileId);
        }
    }

    /**
     * @param file 文件
     * @return 是否存在相同文件名或文件夹名称
     */
    public Boolean isExistFilenameOrFolderName(SysFile file) {
        return fileMapper.findCountNameByPath(file.getPath(), file.getName(), file.getStoreId()) > 0;
    }

    /***
     * 用户当前目录(跨平台)
     * @param currentDirectory 当前目录
     * @return 当前目录
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
     * 通过文件Id获取文件的相对路径
     * @param fileDocument 文件
     * @return 相对路径
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
     * @param file 文件
     * @param to   目标路径
     * @return 中转对象
     */
    private SysFile copyFileEntity(SysFile file, String to) {
        file.setId(null);
        file.setModifyAt(LocalDateTime.now());
        file.setPath(to);
        return file;
    }

    /**
     * @param file 文件
     * @param to   目标路径
     * @return 中转对象
     */
    private SysFile moveFileEntity(SysFile file, String to) {
        file.setModifyAt(LocalDateTime.now());
        file.setPath(to);
        return file;
    }
}
