package com.qzh.eggcloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName StoreDetail
 * @Author DiangD
 * @Date 2021/3/19
 * @Version 1.0
 * @Description 仓库详情vo
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDetail {
    private Long storeId;
    private Integer folderCount;
    private Integer fileCount;
    private Integer textCount;
    private Integer docCount;
    private Integer imageCount;
    private Integer videoCount;
    private Integer audioCount;
    private Integer zipCount;
    private Integer otherCount;
}
