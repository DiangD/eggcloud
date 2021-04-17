package com.qzh.eggcloud.common.resp;

import lombok.Getter;

/**
 * 错误码
 */
@Getter
public enum ErrorCode {

    Success(200, "操作成功"),
    FileUploadSuccess(20000, "文件上传成功"),
    Fail(4000, "操作失败"),
    AccountLocked(4001, "账户已锁定"),
    AccountNotFound(4002, "用户名不存在"),
    Unauthorized(4003, "没有权限"),
    NotLogin(4004, "用户未登录"),
    WrongPassword(4005, "用户名密码错误"),
    InvalidParam(4006, "参数不合法"),
    FrequentOperation(4007, "操作过于频繁"),
    WrongFileFormat(4008, "文件格式错误"),
    UploadFileSizeExceed(4009, "文件大小超过限制"),
    InternalServerError(5000, "服务器异常"),
    DuplicateFolderOrFile(4010, "文件夹或文件名称重复"),
    NoEnoughSpace(4011, "存储空间不足"),
    StopCronTask(4012, "请先停止任务"),
    StartCronTask(4013, "请先开启任务"),

    //token错误
    TokenExpired(3000, "Token已过期，请重新登录"),
    InvalidToken(3001, "无效Token，请重新登录"),
    ;


    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
