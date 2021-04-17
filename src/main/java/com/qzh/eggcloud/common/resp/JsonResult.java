package com.qzh.eggcloud.common.resp;

import lombok.*;

/**
 * @ClassName RespEntity
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 统一返回结果
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult<T> {
    private Integer status;
    private String message;
    private T data;
}


