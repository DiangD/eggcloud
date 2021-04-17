package com.qzh.eggcloud.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName FileStore
 * @Author DiangD
 * @Date 2021/3/10
 * @Version 1.0
 * @Description 抽象的用户存储空间
 **/
@Data
@Builder
public class FileStore implements Serializable {
    private static final long serialVersionUID = 5247351332022706076L;
    /**
     * id
     */
    private Long storeId;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 剩余空间 byte
     */
    private Long occupy;

    /**
     * 存储空间 byte
     */
    private Long size;
}

