package com.qzh.eggcloud.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName CloudConfig
 * @Author DiangD
 * @Date 2021/3/12
 * @Version 1.0
 * @Description egg cloud配置类
 **/
@Configuration
@ConfigurationProperties(prefix = "egg.cloud")
public class CloudConfig {
    public static Long maxSize;
    public static String separator;
    public static Set<String> text;
    public static Set<String> document;
    public static Set<String> image;
    public static Set<String> video;
    public static Set<String> audio;
    public static Set<String> zip;

    public void setMaxSize(String maxSize) {
        int index = maxSize.lastIndexOf("GB");
        //GB转化为字节
        CloudConfig.maxSize = Long.parseLong(maxSize.substring(0, index)) * 1024 * 1024 * 1024;
    }

    public void setText(String textStr) {
        String[] split = textStr.split(",");
        text = new HashSet<>(Arrays.asList(split));
    }

    public void setDocument(String documentStr) {
        String[] split = documentStr.split(",");
        document = new HashSet<>(Arrays.asList(split));
    }

    public void setImage(String imageStr) {
        String[] split = imageStr.split(",");
        image = new HashSet<>(Arrays.asList(split));
    }

    public void setVideo(String videoStr) {
        String[] split = videoStr.split(",");
        video = new HashSet<>(Arrays.asList(split));
    }

    public void setAudio(String audioStr) {
        String[] split = audioStr.split(",");
        audio = new HashSet<>(Arrays.asList(split));
    }

    public void setZip(String zipStr) {
        String[] split = zipStr.split(",");
        zip = new HashSet<>(Arrays.asList(split));
    }

    public  void setSeparator(String separator) {
        CloudConfig.separator = separator;
    }
}
