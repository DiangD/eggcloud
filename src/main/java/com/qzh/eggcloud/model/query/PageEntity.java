package com.qzh.eggcloud.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PageEntity
 * @Author DiangD
 * @Date 2021/3/16
 * @Version 1.0
 * @Description 分页
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageEntity {
    private Integer pageNum;
    private Integer pageSize;
}
