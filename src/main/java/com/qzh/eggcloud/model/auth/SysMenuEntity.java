package com.qzh.eggcloud.model.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName SysMenuEntity
 * @Author DiangD
 * @Date 2021/3/29
 * @Version 1.0
 * @Description
 **/
@Data
public class SysMenuEntity {
    private Long menuId;

    /**
     * 父菜单id
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 权限标识
     */
    private String permission;

    /***
     * 路由地址
     */
    private String path;

    /***
     * 组件路径
     */
    private String component;

    /***
     * 菜单图标
     */
    private String icon;

    /**
     * 权重
     */
    private Integer weight;

    /***
     * 是否隐藏
     */
    private Boolean hide;

    /***
     * 菜单类型 0:菜单，1:按钮
     */
    private Integer menuType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updateAt;

}
