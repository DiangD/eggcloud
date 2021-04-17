package com.qzh.eggcloud.model;


/**
 * cron status enum
 */

public enum JobStatus {
    /**
     * 0=停止
     */
    STOP("0", "停止"),
    /**
     * 1=运行
     */
    RUNNING("1", "运行");

    private final String status;
    private final String code;

    JobStatus(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
