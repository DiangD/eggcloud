package com.qzh.eggcloud.model.query;

/**
 * @ClassName DeletedQuery
 * @Author DiangD
 * @Date 2021/4/13
 * @Version 1.0
 * @Description
 **/
public class DeletedQuery extends QueryBase {
    Long storeId;

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public DeletedQuery() {
    }
}
