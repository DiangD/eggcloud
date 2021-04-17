package com.qzh.eggcloud.model.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @ClassName SysUser
 * @Author DiangD
 * @Date 2021/3/5
 * @Version 1.0
 * @Description user domain
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SysUserEntity implements Serializable {
    private static final long serialVersionUID = 7782188385730896013L;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 4, max = 10, message = "用户名长度在4-10")
    private String username;
    /**
     * 密码
     */
    @Length(min = 6, max = 100, message = "密码长度在6-20")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式错误")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @URL(message = "图片链接格式错误")
    private String avatar;

    /**
     * 状态:ACTIVE 1  PROHIBIT 0
     */
    private Integer status;

    /**
     * 昵称
     */
    @Length(max = 20, message = "昵称长度不能超过20")
    private String nickname;

    /**
     * 签名
     */
    @Length(max = 300, message = "签名长度不能超过300")
    private String signature;

    /**
     * 注册时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createAt;


    /**
     * 个人仓库id
     */
    private Long storeId;

    /**
     * 角色
     */
    private Set<SysRoleEntity> roles;
}
