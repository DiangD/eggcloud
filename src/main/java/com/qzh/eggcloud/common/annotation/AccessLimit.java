package com.qzh.eggcloud.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *请求限制注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    /**
     * 请求限制数
     */
    int limit();


    /**
     * 时间范围
     */
    int timeScope();
}
