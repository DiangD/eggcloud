package com.qzh.eggcloud.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @ClassName ShareFile
 * @Author DiangD
 * @Date 2021/3/15
 * @Version 1.0
 * @Description 分享文件do
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareFile {

    /**
     * 文件id
     */
    @NotNull
    private Long fileId;

    /**
     * 仓库id
     */
    private Long storeId;

    /**
     * 是否为文件夹
     */
    private Boolean isFolder;


    /**
     * 是否需要验证码
     */
    private Boolean hasVerify;


    /**
     * 验证码
     */
    private String code;


    /**
     * 访问key
     */
    private String accessKey;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createAt;

    /**
     * 过期时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expireAt;
}
