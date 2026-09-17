package com.shouyang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Knife4j 接口文档配置类
 * 访问地址：http://localhost:8080/doc.html
 *
 * @author shouyang
 */
@Configuration
public class Knife4jConfig {

    /**
     * 前台接口文档
     */
    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("前台接口")
                .select()
                // 扫描前台 Controller 包
                .apis(RequestHandlerSelectors.basePackage("com.shouyang.controller.api"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 后台接口文档
     */
    @Bean
    public Docket adminDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("后台接口")
                .select()
                // 扫描后台 Controller 包
                .apis(RequestHandlerSelectors.basePackage("com.shouyang.controller.admin"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 接口文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("寿阳文旅云 - API 接口文档")
                .description("基于 Spring Boot + MyBatis-Plus 的文旅云平台后端接口文档")
                .contact(new Contact("shouyang", "", ""))
                .version("1.0.0")
                .build();
    }
}
