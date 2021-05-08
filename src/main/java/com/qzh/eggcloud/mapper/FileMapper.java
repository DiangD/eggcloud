package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.dto.DeletedFile;
import com.qzh.eggcloud.model.query.DeletedQuery;
import com.qzh.eggcloud.model.query.FileQuery;
import com.qzh.eggcloud.model.query.FileSearch;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMapper {
    void removeFileByParentId(@Param("storeId") Long storeId, @Param("parentId") Long parentId);

    void deleteFileById(@Param("id") Long id, @Param("storeId") Long storeId);

    void deleteFileByIds(@Param("fileIds") List<Long> fileIds, @Param("storeId") Long storeId);

    void removeById(@Param("id") Long id, @Param("storeId") Long storeId);

    int insertFileOrFolder(@Param("file") SysFile file);

    int insertFiles(@Param("files") List<SysFile> files);

    int updateFile(@Param("file") SysFile file);

    List<SysFile> findFilesByParentId(@Param("parentId") Long parentId, @Param("storeId") Long storeId);

    SysFile findById(@Param("id") Long id, @Param("storeId") Long storeId);

    SysFile findFileOrFolderByIdDeleted(@Param("id") Long id, @Param("storeId") Long storeId);

    List<DeletedFile> findFilesFoldersDeletedByQuery(@Param("query") DeletedQuery query);

    List<SysFile> findFilesFoldersShouldRemove();

    void restoreFileOrFolderById(@Param("id") Long id, @Param("storeId") Long storeId);

    int restoreFileOrFolderByIds(@Param("fileIds") List<Long> fileIds, @Param("storeId") Long storeId);

    List<SysFile> findFilesBySearchKey(@Param("search") FileSearch fileSearch);

    void removeAllOver30Days();

    int findFoldersCountByStoreId(@Param("storeId") Long storeId);

    int findFilesCountByStoreId(@Param("storeId") Long storeId);

    int findFilesCountByStoreIdAndType(@Param("storeId") Long storeId, @Param("type") Integer type);

    List<SysFile> findByQuery(@Param("query") FileQuery query);

    List<SysFile> findFoldersByStoreId(@Param("storeId") Long storeId);


    //------- rebuild--------//
    List<SysFile> findByPathPrefix(@Param("path") String path, @Param("storeId") Long storeId);

    int findCountNameByPath(@Param("path") String path, @Param("name") String name, @Param("storeId") Long storeId);

    int updateFiles(@Param("storeId") Long storeId, @Param("files") List<SysFile> files);

    List<SysFile> findFolderByPath(@Param("path") String path, @Param("storeId") Long storeId);

    int removeByIds(@Param("storeId") Long storeId, @Param("ids") List<Long> ids);

    int removeByIdsNotStore(@Param("ids") List<Long> ids);

    SysFile findShareFileById(@Param("id") Long id);

    List<SysFile> findByPath(@Param("path") String path, @Param("storeId") Long storeId);
}
