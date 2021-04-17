package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.SysFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileFolderMapper {

    int insertFileFolders(@Param("folders") List<SysFile> folders);

    List<SysFile> getFoldersByParentId(@Param("parentId") Long parentId);

    List<SysFile> findFileFolderByParentId(@Param("storeId") Long storeId, @Param("parentId") Long parentId);

}
