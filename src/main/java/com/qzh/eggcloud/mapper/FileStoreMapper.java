package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.FileStore;
import com.qzh.eggcloud.model.query.StoreQuery;
import com.qzh.eggcloud.model.vo.UserStoreVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileStoreMapper {
    int insertFileStore(@Param("fileStore") FileStore fileStore);

    FileStore findFileStoreById(@Param("storeId") Long storeId);

    int updateFileStore(@Param("fileStore") FileStore fileStore);

    void subOccupy(@Param("fileSize") Long fileSize, @Param("store_id") Long store_id);

    void incrOccupy(@Param("fileSize") Long fileSize, @Param("store_id") Long store_id);

    void updateSize(@Param("size") Long size, @Param("store_id") Long store_id);

    FileStore findFileStoreByUserId(@Param("userId") Long userId);

    List<UserStoreVo> findUserStoreVos(@Param("query") StoreQuery query);

}
