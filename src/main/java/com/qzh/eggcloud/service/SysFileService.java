package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.auth.dto.DeletedFile;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.SysFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName SysFileService
 * @Author DiangD
 * @Date 2021/3/13
 * @Version 1.0
 * @Description
 **/
public interface SysFileService {
    void deleteFileOrFolder(@Param("file") SysFile file);

    void deleteFilesOrFolders(Long storeId, List<Long> fileIds);

    void updateFileOrFolder(@Param("file") SysFile file) throws BaseException;

    void addFileOrFolder(@Param("file") SysFile file) throws BaseException;

    PageInfo<SysFile> findTypeFiles(@Param("storeId") Long storeId, @Param("type") Integer type, PageEntity pageEntity);

    PageInfo<DeletedFile> getTrashBin(DeletedQuery query, PageEntity pageEntity);

    void removeFileOrFolder(@Param("fileIds") List<Long> fileIds, @Param("storeId") Long storeId);

    void restoreFileOrFolder(List<Long> fileIds, @Param("storeId") Long storeId);

    PageInfo<SysFile> searchFiles(FileSearch fileSearch, PageEntity pageEntity);

    void removeFilesDeletedOver30Days();

    PageInfo<SysFile> findUserStorePage(FileQuery query, PageEntity pageEntity);

    SysFile findFileOrFolder(Long id, Long storeId);

    List<SysFile> findDirPath(Long folderId, Long storeId);

    List<SysFile> getFolderTree(Long storeId, Long parentId);

    void moveFiles(Long storeId, Long parentId, List<Long> fileIds) throws BaseException;

}
