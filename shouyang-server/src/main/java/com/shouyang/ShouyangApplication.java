package com.shouyang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 寿阳文旅云 - 启动类
 *
 * @author shouyang
 */

@SpringBootApplication
@MapperScan("com.shouyang.mapper")
public class ShouyangApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShouyangApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  寿阳文旅云后端服务启动成功！");
        System.out.println("  接口文档：http://localhost:8080/doc.html");
        System.out.println("==============================================");
    }
}
