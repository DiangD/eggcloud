package com.qzh.eggcloud.model;

import lombok.Getter;

/**
 * file content type enum
 */
@Getter
public enum FileType {
    Text(0, "text"),
    Doc(1, "doc"),
    Image(2, "image"),
    Audio(3, "audio"),
    Video(4, "video"),
    Zip(5, "zip"),
    Other(6, "other"),
    ;

    Integer typeId;
    String typename;

    FileType(int typeId, String typename) {
        this.typeId = typeId;
        this.typename = typename;
    }
}
