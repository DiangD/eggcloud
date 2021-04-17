package com.qzh.eggcloud.model.query;

import lombok.EqualsAndHashCode;

/**
 * @ClassName FileQuery
 * @Author DiangD
 * @Date 2021/4/3
 * @Version 1.0
 * @Description
 **/
@EqualsAndHashCode(callSuper = true)
public class FileQuery extends QueryBase {
    Long storeId;
    Long folderId;
    String queryFileType;

    public String getQueryFileType() {
        return queryFileType;
    }

    public void setQueryFileType(String queryFileType) {
        this.queryFileType = queryFileType;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public FileQuery() {
    }
}

