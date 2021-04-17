package com.qzh.eggcloud.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @ClassName File
 * @Author DiangD
 * @Date 2021/3/10
 * @Version 1.0
 * @Description file domain
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SysFile extends FileBase {
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createAt;

    /**
     * 修改时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifyAt;

    /**
     * 后缀名
     */
    private String extension;
    /**
     * 仓库id
     */
    private Long storeId;

    /**
     * 父文件夹id
     */
    private Long parentId;


    /**
     * dfs中的分组
     */
    private String group;

    /**
     * 路径
     */
    private String path;


    /**
     * 预览路径
     */
    private String url;


    /**
     * 是否删除
     */
    private Integer deleted;


    private String contentType;

    private String thumbnail;
}
