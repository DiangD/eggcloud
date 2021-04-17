package com.qzh.eggcloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName FileBase
 * @Author DiangD
 * @Date 2021/3/13
 * @Version 1.0
 * @Description file base class do
 **/
@Data
@AllArgsConstructor
public class FileBase {
    /**
     * id
     */
    private Long id;
    /**
     * 是否是文件夹
     */
    private Boolean isFolder;
    /**
     * 文件夹名称或文件名
     */
    private String name;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 文件md5
     */
    private String md5;

    /**
     * 文件类型
     */
    private Integer type;

    /**
     * 创建时间或上传时间
     */
    private LocalDateTime createAt;

    /**
     * 修改时间
     */
    private LocalDateTime modifyAt;

    public FileBase() {
    }
}
