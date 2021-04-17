package com.qzh.eggcloud.dfs;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @ClassName EggDfsConfig
 * @Author DiangD
 * @Date 2021/3/9
 * @Version 1.0
 * @Description dfs配置文件
 **/
@Getter
@Configuration
@ConfigurationProperties(prefix = "egg.dfs")
@PropertySource(value = "classpath:application.yml", encoding = "utf-8")
@EnableConfigurationProperties(EggDfsConfig.class)
public class EggDfsConfig {
    public static String url;
    public static final String upload = "/upload";
    public static final String delete = "/delete";
    public static final String download = "/download";

    public void setUrl(String url) {
        EggDfsConfig.url = url;
    }


    public static String getUploadUrl() {
        return url + upload;
    }

    public static String getDeleteUrl() {
        return url + delete;
    }

    public static String getDownloadUrl() {
        return url + download;
    }
}
