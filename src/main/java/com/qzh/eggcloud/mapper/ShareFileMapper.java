package com.qzh.eggcloud.mapper;

import com.qzh.eggcloud.model.ShareFile;
import com.qzh.eggcloud.model.dto.ShareDTO;
import com.qzh.eggcloud.model.query.ShareQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareFileMapper {
    int insertShareFile(@Param("shareFile") ShareFile shareFile);

    ShareDTO findShareFileByAccessKey(@Param("accessKey") String accessKey);

    void deleteShareFileByAccessKey(@Param("accessKey") String accessKey, @Param("storeId") Long storeId);

    List<ShareFile> findShareFilesByStoreId(@Param("storeId") Long storeId);

    ShareFile findShareFileByFileId(@Param("fileId") Long fileId, @Param("storeId") Long storeId);

    void deleteByExpireAt();

    List<ShareDTO> findShareByQuery(@Param("query") ShareQuery query);

    void deleteShareFileByAccessKeys(@Param("accessKeys") List<String> accessKey, @Param("storeId") Long storeId);

}
