package com.qzh.eggcloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.auth.SysUserDetail;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.utils.SecurityUtil;
import com.qzh.eggcloud.model.*;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.query.StoreQuery;
import com.qzh.eggcloud.model.vo.UserStoreVo;
import com.qzh.eggcloud.service.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName FileStoreServiceImpl
 * @Author DiangD
 * @Date 2021/3/11
 * @Version 1.0
 * @Description
 **/
@Service
@Slf4j
public class FileStoreServiceImpl extends BaseService implements FileStoreService {

    /**
     * @param fileStore 仓库
     *                  新增仓库
     */
    @Override
    public void addFileStore(FileStore fileStore) {
        fileStoreMapper.insertFileStore(fileStore);
    }


    /**
     * @param folder 文件夹
     *               移除文件夹
     */
    @Override
    @Transactional
    public void removeFileFolder(SysFile folder) {
        //递归
        removeFolder(folder);
    }

    /**
     * @param storeId 仓库id
     * @param size    文件大小
     * @return 是否有空间
     */
    @Override
    public Boolean hasEnoughSpace(Long storeId, Long size) {
        FileStore store = fileStoreMapper.findFileStoreById(storeId);
        log.debug("store info{}", store);
        return store.getOccupy() + size <= store.getSize();
    }

    /**
     * @param userId 用户id
     * @return store信息
     * @throws BaseException BaseException
     */
    @Override
    public FileStore getStoreInfo(Long userId) throws BaseException {
        SysUserDetail userDetail = SecurityUtil.getSysUserDetail();
        if (!userDetail.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.InvalidParam);
        }
        return fileStoreMapper.findFileStoreById(userDetail.getStoreId());
    }

    /**
     * @param query      store query model
     * @param pageEntity page
     * @return page
     */
    @Override
    public PageInfo<UserStoreVo> getUserStores(StoreQuery query, PageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getPageNum(), pageEntity.getPageSize());
        List<UserStoreVo> storeVos = fileStoreMapper.findUserStoreVos(query);
        return PageInfo.of(storeVos);
    }

    /**
     * @param store 仓库
     * @throws BaseException 统一异常
     */
    @Override
    public void updateFileStore(FileStore store) throws BaseException {
        if (fileStoreMapper.findFileStoreById(store.getStoreId()) == null) {
            throw new BaseException(ErrorCode.InvalidParam);
        }
        fileStoreMapper.updateFileStore(store);
    }

    /**
     * @param storeId 仓库id
     * @return 仓库详细信息
     * @throws BaseException BaseException
     */
    @Override
    public StoreDetail getStoreDetail(Long storeId) throws BaseException {
        if (fileStoreMapper.findFileStoreById(storeId) == null) {
            throw new BaseException(ErrorCode.InvalidParam);
        }
        int folderCount = fileMapper.findFoldersCountByStoreId(storeId);
        int fileCount = fileMapper.findFilesCountByStoreId(storeId);
        int textCount = fileMapper.findFilesCountByStoreIdAndType(storeId, FileType.Text.getTypeId());
        int docCount = fileMapper.findFilesCountByStoreIdAndType(storeId, FileType.Doc.getTypeId());
        int imageCount = fileMapper.findFilesCountByStoreIdAndType(storeId, FileType.Image.getTypeId());
        int audioCount = fileMapper.findFilesCountByStoreIdAndType(storeId, FileType.Audio.getTypeId());
        int videoCount = fileMapper.findFilesCountByStoreIdAndType(storeId, FileType.Video.getTypeId());
        int zipCount = fileMapper.findFilesCountByStoreIdAndType(storeId, FileType.Zip.getTypeId());
        int otherCount = fileMapper.findFilesCountByStoreIdAndType(storeId, FileType.Other.getTypeId());
        StoreDetail storeDetail = StoreDetail.builder()
                .fileCount(fileCount)
                .folderCount(folderCount)
                .textCount(textCount)
                .docCount(docCount)
                .imageCount(imageCount)
                .audioCount(audioCount)
                .videoCount(videoCount)
                .zipCount(zipCount)
                .otherCount(otherCount)
                .storeId(storeId)
                .build();
        return storeDetail;
    }

    @Override
    public List<SysFile> findAllUserFolders(Long storeId) {
        return fileMapper.findFoldersByStoreId(storeId);
    }

    /**
     * @param folder 文件夹
     *               移除文件夹
     */
    private void removeFolder(SysFile folder) {
        if (folder == null) {
            return;
        }
        fileMapper.removeFileOrFolderById(folder.getId(), folder.getStoreId());
        List<SysFile> files = fileMapper.findFilesByParentId(folder.getId(), folder.getStoreId());
        long sizeSum = 0;
        for (SysFile file : files) {
            sizeSum += file.getSize();
        }
        fileMapper.removeFileByParentId(folder.getStoreId(), folder.getId());
        fileStoreMapper.subOccupy(sizeSum, folder.getStoreId());
        List<SysFile> children = fileFolderMapper.findFileFolderByParentId(folder.getStoreId(), folder.getId());
        if (children.size() > 0) {
            for (SysFile child : children) {
                removeFolder(child);
            }
        }
    }


    /**
     * @param folder 文件夹
     * @throws BaseException 新建文件夹
     */
    public SysFile newFileFolder(SysFile folder) throws BaseException {
        if (isExistFolderName(folder)) {
            throw new BaseException(ErrorCode.DuplicateFolderOrFile);
        }
        folder.setIsFolder(true);
        folder.setCreateAt(LocalDateTime.now());
        folder.setModifyAt(LocalDateTime.now());
        fileMapper.insertFileOrFolder(folder);
        return fileMapper.findFileOrFolderById(folder.getId(), folder.getStoreId());
    }

    /**
     * @param folder 文件夹
     * @throws BaseException BaseException
     *                       复制文件夹
     */
    @Override
    @Transactional
    public void copyFileFolder(SysFile folder) throws BaseException {
        if (isExistFolderName(folder)) {
            throw new BaseException(ErrorCode.DuplicateFolderOrFile);
        }
        //root
        SysFile folderById = fileMapper.findFileOrFolderById(folder.getId(), folder.getStoreId());
        //递归
        recurCopyFolder(folderById, folder.getParentId());
    }

    /**
     * @param folder   文件夹
     * @param parentId 新父文件夹id
     */
    // folderId parentId
    private void recurCopyFolder(SysFile folder, Long parentId) throws BaseException {
        //exit
        if (folder == null) {
            return;
        }
        //原文件夹id
        long folderId = folder.getId();
        //设置新父文件夹
        folder.setParentId(parentId);
        folder.setId(null);
        fileMapper.insertFileOrFolder(folder);
        //获取原id文件夹下的所有文件
        List<SysFile> files = fileMapper.findFilesByParentId(folderId, folder.getStoreId());
        long sizeSum = 0;
        if (files.size() > 0) {
            for (SysFile file : files) {
                //设置新id
                sizeSum += file.getSize();
                file.setParentId(folder.getId());
            }
            //检查空间
            if (!hasEnoughSpace(folder.getStoreId(), sizeSum)) {
                throw new BaseException(ErrorCode.NoEnoughSpace);
            }
            fileMapper.insertFiles(files);
            fileStoreMapper.incrOccupy(sizeSum, folder.getStoreId());
        }

        //获取旧原id下的所有文件夹
        List<SysFile> folderChildren = fileFolderMapper.findFileFolderByParentId(folder.getStoreId(), folderId);
        log.info("folderChildren:{}", folderChildren);
        if (folderChildren.size() > 0) {
            parentId = folder.getId();
            //递归
            for (SysFile child : folderChildren) {
                recurCopyFolder(child, parentId);
            }
        }
    }

    /**
     * @param folder 文件夹
     * @return 是否存在同名文件夹
     */
    public boolean isExistFolderName(SysFile folder) {
        folder.setIsFolder(true);
        return fileMapper.findFilesOrFoldersByNameAndParentId(folder).size() > 0;
    }
}
