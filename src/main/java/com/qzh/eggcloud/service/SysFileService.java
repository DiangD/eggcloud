package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.auth.dto.DeletedFile;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import com.qzh.eggcloud.model.query.PageEntity;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName SysFileService
 * @Author DiangD
 * @Date 2021/3/13
 * @Version 1.0
 * @Description
 **/
public interface SysFileService {
    void deleteFilesOrFolders(Long storeId, List<Long> fileIds);

    void updateFileOrFolder(@Param("file") SysFile file) throws BaseException;

    void addFileOrFolder(@Param("file") SysFile file) throws BaseException;

    PageInfo<DeletedFile> getTrashBin(DeletedQuery query, PageEntity pageEntity);

    void removeFiles(@Param("fileIds") List<Long> fileIds, @Param("storeId") Long storeId);

    void restoreFileOrFolder(List<Long> fileIds, @Param("storeId") Long storeId);

    PageInfo<SysFile> searchFiles(FileSearch fileSearch, PageEntity pageEntity);

    void removeFilesDeletedOver30Days();

    PageInfo<SysFile> findUserStorePage(FileQuery query, PageEntity pageEntity);

    SysFile findFileOrFolder(Long id, Long storeId);


    void moveFiles(Long storeId, Long parentId, List<Long> fileIds) throws BaseException;

    PageInfo<SysFile> searchOpen(FileQuery query, PageEntity pageEntity) throws BaseException;

    List<SysFile> getFolderTree(Long storeId, Long parentId);

    void packageDownload(HttpServletRequest request, HttpServletResponse response, List<Long> fileIds) throws IOException, BaseException;

    void publicPackageDownload(HttpServletRequest request, HttpServletResponse response, List<Long> fileIds) throws IOException, BaseException;

    SysFile getShareFile(Long id);
}
