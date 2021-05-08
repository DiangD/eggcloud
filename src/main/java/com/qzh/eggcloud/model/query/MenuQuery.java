package com.qzh.eggcloud.model.query;

/**
 * @ClassName MenuQuery
 * @Author DiangD
 * @Date 2021/5/5
 * @Version 1.0
 * @Description
 **/
public class MenuQuery extends QueryBase {
    String name;
    String path;

    public MenuQuery(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public MenuQuery() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
