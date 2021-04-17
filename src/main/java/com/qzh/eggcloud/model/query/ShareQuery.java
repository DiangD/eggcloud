package com.qzh.eggcloud.model.query;

import lombok.EqualsAndHashCode;

/**
 * @ClassName ShareQuery
 * @Author DiangD
 * @Date 2021/4/12
 * @Version 1.0
 * @Description
 **/
@EqualsAndHashCode(callSuper = true)
public class ShareQuery extends QueryBase{
    Long storeId;

    public ShareQuery(Long storeId) {
        this.storeId = storeId;
    }

    public ShareQuery() {
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}
