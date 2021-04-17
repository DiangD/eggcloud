package com.qzh.eggcloud.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UserStoreVo
 * @Author DiangD
 * @Date 2021/3/19
 * @Version 1.0
 * @Description user store vo
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStoreVo {
    private Long userId;
    private String username;
    private Long storeId;
    private Long occupy;
    private Long size;
}
