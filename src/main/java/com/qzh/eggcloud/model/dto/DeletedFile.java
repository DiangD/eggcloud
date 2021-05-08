package com.qzh.eggcloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @ClassName DeletedFile
 * @Author DiangD
 * @Date 2021/4/13
 * @Version 1.0
 * @Description
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeletedFile {
    private String fileId;
    private Boolean isFolder;
    private String filename;
    private String contentType;
    private String extension;
    private Long size;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime deleteAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expireAt;

    public void setDeleteAt(LocalDateTime deleteAt) {
        this.deleteAt = deleteAt;
        //默认30天有效期
        this.expireAt = deleteAt.plusDays(10);
    }
}
