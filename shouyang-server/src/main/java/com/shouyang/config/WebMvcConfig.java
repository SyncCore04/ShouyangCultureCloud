package com.shouyang.config;

import com.shouyang.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置静态资源映射、拦截器等
 *
 * @author shouyang
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 上传文件存储路径（从配置文件读取，默认项目根目录下的 uploads）
     */
    @Value("${file.upload-path:./uploads/}")
    private String uploadPath;

    /**
     * 静态资源映射
     * 将 /uploads/** 请求映射到本地文件目录，实现上传文件的 HTTP 访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件目录
        // file: 前缀表示从文件系统读取，末尾必须加 /
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }

    /**
     * 注册 JWT 拦截器
     * 拦截需要登录的接口路径，白名单放行登录注册等公开接口
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截需要登录的路径
                .addPathPatterns(
                        "/api/user/**",           // 用户相关（排除登录注册）
                        "/api/admin/**",          // 后台管理（排除登录）
                        "/api/favorite/**",       // 收藏
                        "/api/activity/register", // 活动报名
                        "/api/activity/my/**"     // 我的报名
                )
                // 白名单：不需要登录的路径
                .excludePathPatterns(
                        "/api/user/login",        // 用户登录
                        "/api/user/register",     // 用户注册
                        "/api/admin/login"        // 管理员登录
                );
    }
}
