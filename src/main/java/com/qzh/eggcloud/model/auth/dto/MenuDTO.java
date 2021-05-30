package com.qzh.eggcloud.model.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName MenuDTO
 * @Author DiangD
 * @Date 2021/3/29
 * @Version 1.0
 * @Description
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO implements Comparable<MenuDTO> {
    private Long menuId;

    /**
     * 父菜单id
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
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
     * 菜单类型 0:菜单，1:按钮
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    /***
     * 是否隐藏
     */
    @NotNull(message = "是否隐藏不能为空")
    private Boolean hide;

    /***
     * 子菜单
     */
    private List<MenuDTO> children;
    /***
     * 角色是否拥有该菜单
     */

    private Boolean checked;
    /***
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    private Long userId;

    @Override
    public int compareTo(MenuDTO o) {
        return this.getWeight().compareTo(o.getWeight());
    }
}
