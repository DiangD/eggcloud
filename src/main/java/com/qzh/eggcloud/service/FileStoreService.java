package com.qzh.eggcloud.service;

import com.github.pagehelper.PageInfo;
import com.qzh.eggcloud.common.exception.BaseException;
import com.qzh.eggcloud.model.FileStore;
import com.qzh.eggcloud.model.query.PageEntity;
import com.qzh.eggcloud.model.StoreDetail;
import com.qzh.eggcloud.model.SysFile;
import com.qzh.eggcloud.model.query.StoreQuery;
import com.qzh.eggcloud.model.vo.UserStoreVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileStoreService {
    void addFileStore(@Param("fileStore") FileStore fileStore);

    SysFile newFileFolder(@Param("folder") SysFile folder) throws BaseException;

    void removeFileFolder(@Param("folder") SysFile folder);

    Boolean hasEnoughSpace(Long storeId, Long size);

    FileStore getStoreInfo(@Param("userId") Long userId) throws BaseException;

    PageInfo<UserStoreVo> getUserStores(StoreQuery query, PageEntity pageEntity);

    void updateFileStore(@Param("store") FileStore store) throws BaseException;

    StoreDetail getStoreDetail(@Param("storeId") Long storeId) throws BaseException;

    List<SysFile> findAllUserFolders(Long storeId);

}
