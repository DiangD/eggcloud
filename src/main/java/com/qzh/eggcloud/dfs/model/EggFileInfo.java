package com.qzh.eggcloud.dfs.model;

import lombok.Data;

/**
 * @ClassName EggFileInfo
 * @Author DiangD
 * @Date 2021/3/9
 * @Version 1.0
 * @Description egg dfs file
 **/
@Data
public class EggFileInfo {
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件的重命名
     */
    private String rename;
    /**
     * 保存路径
     */
    private String path;
    /**
     * 预览路径
     */
    private String url;
    /**
     * md5
     */
    private String md5;
    /**
     * 分组
     */
    private String group;
    /**
     * 文件大小
     */
    private Long size;
}
