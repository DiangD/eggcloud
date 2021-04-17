package com.qzh.eggcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableConfigurationProperties
@MapperScan(basePackages = "com.qzh.eggcloud.mapper")
@EnableAspectJAutoProxy
public class EggCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(EggCloudApplication.class, args);
    }

}
