package com.qzh.eggcloud.model.query;

/**
 * @ClassName QueryBase
 * @Author DiangD
 * @Date 2021/4/3
 * @Version 1.0
 * @Description
 **/
public class QueryBase {
    public String sortProp;
    public String sortOrder;

    public String getSortProp() {
        return sortProp;
    }

    public QueryBase() {
    }

    public void setSortProp(String sortProp) {
        this.sortProp = sortProp;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        switch (sortOrder) {
            case "descending":
                this.sortOrder = "desc";
                return;
            case "ascending":
                this.sortOrder = "asc";
                return;
            default:
                this.sortOrder = sortOrder;
        }

    }
}
