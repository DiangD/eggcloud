package com.qzh.eggcloud.common.exception;

import com.qzh.eggcloud.common.resp.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName BaseException
 * @Author DiangD
 * @Date 2021/3/7
 * @Version 1.0
 * @Description 基类exception
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends Exception {
    private static final long serialVersionUID = 1215099577333028980L;
    private Integer code;
    private String message;

    public BaseException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public BaseException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
