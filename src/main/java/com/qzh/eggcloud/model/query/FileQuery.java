package com.qzh.eggcloud.model.query;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName FileQuery
 * @Author DiangD
 * @Date 2021/4/3
 * @Version 1.0
 * @Description
 **/
@EqualsAndHashCode(callSuper = true)
public class FileQuery extends QueryBase {
    Long userId;
    Long storeId;
    Long folderId;
    String queryFileType;
    /***
     * 当前目录,用户的网盘目录,如果为空则为"/"
     */
    String currentDirectory;

    /***
     * 是否是文件夹
     */
    Boolean isFolder;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public Boolean getFolder() {
        return isFolder;
    }

    public void setFolder(Boolean folder) {
        isFolder = folder;
    }

    public FileQuery() {
    }
}

