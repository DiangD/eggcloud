package com.qzh.eggcloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.model.auth.dto.DeletedFile;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.service.FileStoreService;
import com.qzh.eggcloud.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    /**
     * @param file 文件
     *             删除文件或文件夹
     */
    @Override
    public void deleteFileOrFolder(SysFile file) {
        fileMapper.deleteFileById(file.getId(), file.getStoreId());
    }

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
        fileMapper.updateFileOrFolder(file);
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
     * @param storeId    仓库id
     * @param type       文件类型
     * @param pageEntity 页数
     * @return 分页
     */
    @Override
    public PageInfo<SysFile> findTypeFiles(Long storeId, Integer type, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysFile> files = fileMapper.findFilesByTypeByStoreId(storeId, type);
        return new PageInfo<>(files);
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
    public void removeFileOrFolder(List<Long> fileIds, Long storeId) {
        for (Long id : fileIds) {
            SysFile file = fileMapper.findFileOrFolderByIdDeleted(id, storeId);
            if (file.getIsFolder()) {
                storeService.removeFileFolder(file);
                return;
            }
            fileStoreMapper.subOccupy(file.getSize(), file.getStoreId());
            fileMapper.removeFileOrFolderById(file.getId(), file.getStoreId());
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
        fileMapper.removeAllOver30Days();
    }

    @Override
    public PageInfo<SysFile> findUserStorePage(FileQuery fileQuery, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<SysFile> files = fileMapper.findFilesAndFoldersByParentId(fileQuery);
        return PageInfo.of(files);
    }

    @Override
    public SysFile findFileOrFolder(Long id, Long storeId) {
        return fileMapper.findFileOrFolderById(id, storeId);
    }

    @Override
    public List<SysFile> findDirPath(Long folderId, Long storeId) {
        ArrayList<SysFile> files = Lists.newArrayList();
        travelDir(folderId, storeId, files);
        Collections.reverse(files);
        return files;
    }

    @Override
    public List<SysFile> getFolderTree(Long storeId, Long parentId) {
        return fileMapper.findFoldersByParentId(storeId, parentId);
    }

    @Override
    public void moveFiles(Long storeId, Long parentId, List<Long> fileIds) throws BaseException {
        List<SysFile> list = Lists.newArrayList();
        for (Long fileId : fileIds) {
            SysFile file = fileMapper.findFileOrFolderById(fileId, storeId);
            list.add(file);
        }
        if (checkMoveOrCopyPath(storeId, parentId, list)) {
            throw new BaseException(ErrorCode.Fail.getCode(), "不能将文件移动到自身或其子目录下");
        }
        fileMapper.updateFilesParentId(storeId, parentId, fileIds);
    }

    private void travelDir(Long folderId, Long storeId, List<SysFile> list) {
        SysFile folder = fileMapper.findFileOrFolderById(folderId, storeId);
        if (folder == null) {
            return;
        }
        list.add(folder);
        if (folder.getParentId() != null) {
            travelDir(folder.getParentId(), storeId, list);
        }
    }


    /**
     * @param storeId 仓库Id
     * @param fileIds 文件id列表
     * @throws BaseException 自定义异常
     */
    @Transactional
    public void copyFileOrFolder(Long storeId, Long parentId, List<Long> fileIds) throws BaseException {
        List<SysFile> list = Lists.newArrayList();
        for (Long fileId : fileIds) {
            SysFile file = fileMapper.findFileOrFolderById(fileId, storeId);
            list.add(file);
        }

        if (checkMoveOrCopyPath(storeId, parentId, list)) {
            throw new BaseException(ErrorCode.Fail.getCode(), "不能将文件复制到自身或其子目录下");
        }

        for (SysFile file : list) {
            file.setParentId(parentId);
            if (file.getIsFolder()) {
                storeService.copyFileFolder(file);
                return;
            }
            copyFile(file);
        }
    }

    public boolean checkMoveOrCopyPath(Long storeId, Long parentId, List<SysFile> files) {
        Set<Long> set = Sets.newHashSet();
        for (SysFile file : files) {
            if (file.getIsFolder()) {
                set.add(file.getId());
                Set<Long> childrenIds = getFolderAllChildrenIds(storeId, file.getId());
                set.addAll(childrenIds);
            }
        }
        return set.contains(parentId);
    }

    public Set<Long> getFolderAllChildrenIds(Long storeId, Long parentId) {
        HashSet<SysFile> fileHashSet = Sets.newHashSet();
        recurFolderChildren(storeId, parentId, fileHashSet);
        HashSet<Long> idSet = Sets.newHashSet();
        fileHashSet.forEach(folder -> {
            idSet.add(folder.getId());
        });
        return idSet;
    }

    private void recurFolderChildren(Long storeId, Long parentId, Set<SysFile> set) {
        if (parentId == null) {
            return;
        }
        List<SysFile> folders = fileMapper.findFoldersByParentId(storeId, parentId);
        if (folders == null || folders.size() <= 0) {
            return;
        }
        set.addAll(folders);
        folders.forEach(folder -> {
            recurFolderChildren(storeId, folder.getId(), set);
        });
    }
//


    /**
     * @param file 文件
     * @throws BaseException 自定义异常
     */
    public void copyFile(SysFile file) throws BaseException {
        if (isExistFilenameOrFolderName(file)) {
            throw new BaseException(ErrorCode.DuplicateFolderOrFile);
        }

        SysFile fileById = fileMapper.findFileOrFolderById(file.getId(), file.getStoreId());
        fileById.setParentId(file.getParentId());
        fileById.setName(file.getName());

        if (!storeService.hasEnoughSpace(file.getStoreId(), file.getSize())) {
            throw new BaseException(ErrorCode.NoEnoughSpace);
        }
        fileMapper.insertFileOrFolder(fileById);
    }

    /**
     * @param file 文件
     * @return 是否存在相同文件名或文件夹名称
     */
    public Boolean isExistFilenameOrFolderName(SysFile file) {
        return fileMapper.findFilesOrFoldersByNameAndParentId(file).size() > 0;
    }
}
