package com.qzh.eggcloud.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName StoreQuery
 * @Author DiangD
 * @Date 2021/3/19
 * @Version 1.0
 * @Description store query do
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreQuery {
    private Long userId;
    private Long storeId;
    private String username;
}
