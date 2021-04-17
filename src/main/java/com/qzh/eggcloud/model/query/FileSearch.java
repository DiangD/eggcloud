package com.qzh.eggcloud.model.query;

import lombok.EqualsAndHashCode;

/**
 * @ClassName FileSearch
 * @Author DiangD
 * @Date 2021/4/8
 * @Version 1.0
 * @Description
 **/
@EqualsAndHashCode(callSuper = true)
public class FileSearch extends QueryBase{
    String searchKey;

    Long storeId;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public FileSearch() {
    }
}
