package com.qzh.eggcloud.common.config;

import com.qzh.eggcloud.common.interceptor.AccessLimitInterceptor;
import com.qzh.eggcloud.common.interceptor.AvatarUploadInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @ClassName WebConfig
 * @Author DiangD
 * @Date 2021/3/7
 * @Version 1.0
 * @Description web配置
 **/
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    @Autowired
    private AvatarUploadInterceptor avatarUploadInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(accessLimitInterceptor).addPathPatterns("/verify/code");
        registry.addInterceptor(avatarUploadInterceptor).addPathPatterns("/u/upload/avatar");
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
