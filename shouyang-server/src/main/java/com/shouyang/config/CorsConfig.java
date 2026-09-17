package com.shouyang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 允许前端开发服务器跨域访问后端接口
 *
 * @author shouyang
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许所有来源（开发环境方便调试，生产环境建议指定具体域名）
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                // 允许所有请求头
                .allowedHeaders("*")
                // 暴露响应头（前端需要读取的头）
                .exposedHeaders("Authorization", "Content-Disposition")
                // 允许携带凭证（Cookie、Authorization 等）
                .allowCredentials(true)
                // 预检请求缓存时间（秒），减少 OPTIONS 请求次数
                .maxAge(3600);
    }
}
