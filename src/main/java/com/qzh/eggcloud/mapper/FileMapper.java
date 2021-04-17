package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.auth.dto.DeletedFile;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMapper {
    void deleteFileOrFolderByParentId(@Param("file") SysFile file);

    void removeFileByParentId(@Param("storeId") Long storeId, @Param("parentId") Long parentId);

    void removeFileAndFolderByParentId(@Param("parentId") Long parentId);

    void deleteFileById(@Param("id") Long id, @Param("storeId") Long storeId);

    void deleteFileByIds(@Param("fileIds") List<Long> fileIds, @Param("storeId") Long storeId);

    void removeFileOrFolderById(@Param("id") Long id, @Param("storeId") Long storeId);

    int insertFileOrFolder(@Param("file") SysFile file);

    int insertFiles(@Param("files") List<SysFile> files);

    int updateFileOrFolder(@Param("file") SysFile file);

    List<SysFile> findFilesByParentId(@Param("parentId") Long parentId, @Param("storeId") Long storeId);

    List<SysFile> findFilesOrFoldersByNameAndParentId(@Param("file") SysFile file);

    SysFile findFileOrFolderById(@Param("id") Long id, @Param("storeId") Long storeId);

    SysFile findFileOrFolderByIdDeleted(@Param("id") Long id, @Param("storeId") Long storeId);

    List<SysFile> findFilesByTypeByStoreId(@Param("storeId") Long storeId, @Param("type") Integer type);

    List<DeletedFile> findFilesFoldersDeletedByQuery(@Param("query") DeletedQuery query);

    List<SysFile> findFilesFoldersShouldRemove();

    void restoreFileOrFolderById(@Param("id") Long id, @Param("storeId") Long storeId);

    int restoreFileOrFolderByIds(@Param("fileIds") List<Long> fileIds, @Param("storeId") Long storeId);

    List<SysFile> findFilesBySearchKey(@Param("search") FileSearch fileSearch);

    void removeAllOver30Days();

    int findFoldersCountByStoreId(@Param("storeId") Long storeId);

    int findFilesCountByStoreId(@Param("storeId") Long storeId);

    int findFilesCountByStoreIdAndType(@Param("storeId") Long storeId, @Param("type") Integer type);

    List<SysFile> findFilesAndFoldersByParentId(@Param("query") FileQuery query);

    List<SysFile> findFoldersByStoreId(@Param("storeId") Long storeId);

    List<SysFile> findFoldersByParentId(@Param("storeId") Long storeId, @Param("parentId") Long parentId);

    int updateFilesParentId(@Param("storeId") Long storeId, @Param("parentId") Long parentId, @Param("fileIds") List<Long> fileIds);

}
