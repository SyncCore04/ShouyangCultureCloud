package com.shouyang.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器
 * <p>
 * 运行 main 方法即可自动生成 18 张表的 entity / mapper / service / serviceImpl / controller
 * <p>
 * 运行方式：在 IDEA 中右键本文件 → Run 'CodeGenerator.main()'
 * 或在项目根目录执行：mvn compile exec:java -Dexec.mainClass="com.shouyang.generator.CodeGenerator"
 * <p>
 * 注意：运行前请确保 MySQL 已启动，数据库 shouyang_culture 已创建
 *
 * @author shouyang
 */
public class CodeGenerator {

    // ==================== 数据库连接配置 ====================
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shouyang_culture"
            + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";

    // ==================== 需要生成代码的表 ====================
    private static final String[] TABLES = {
            "sys_user",          // 用户表
            "sys_admin",         // 管理员表
            "banner",            // 轮播图表
            "news_category",     // 资讯分类表
            "news",              // 资讯表
            "ticket",            // 票务表
            "venue",             // 场馆表
            "activity",          // 活动表
            "activity_register", // 活动报名表
            "pavilion",          // 数字展馆表
            "heritage",          // 非遗文化表
            "product",           // 文创商品表
            "scenic_spot",       // 景点表
            "food",              // 美食表
            "hotel",             // 酒店表
            "travel_guide",      // 旅游攻略表
            "organization",      // 文旅单位表
            "favorite"           // 收藏表
    };

    // ==================== 包名配置 ====================
    private static final String PARENT_PACKAGE = "com.shouyang";

    // ==================== 输出路径 ====================
    // 项目根目录（在 IDEA 中运行时 user.dir 即为项目根目录）
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String JAVA_OUTPUT_PATH = PROJECT_PATH + "/src/main/java";
    private static final String XML_OUTPUT_PATH = PROJECT_PATH + "/src/main/resources/mapper";

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  开始生成代码...");
        System.out.println("  项目路径: " + PROJECT_PATH);
        System.out.println("  生成表数量: " + TABLES.length);
        System.out.println("==============================================");

        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                // ==================== 全局配置 ====================
                .globalConfig(builder -> {
                    builder.author("shouyang")                    // 作者
                            .outputDir(JAVA_OUTPUT_PATH)          // 输出目录
                            .commentDate("yyyy-MM-dd")             // 日期格式
                            .disableOpenDir();                       // 生成后不自动打开目录
                })

                // ==================== 包配置 ====================
                .packageConfig(builder -> {
                    builder.parent(PARENT_PACKAGE)               // 父包名
                            .entity("entity")                       // 实体类包
                            .mapper("mapper")                       // Mapper 包
                            .service("service")                     // Service 包
                            .serviceImpl("service.impl")            // ServiceImpl 包
                            .controller("controller")                // Controller 包
                            // XML 文件输出路径（虽然禁用了 XML 生成，但配置保留）
                            .pathInfo(Collections.singletonMap(OutputFile.xml, XML_OUTPUT_PATH));
                })

                // ==================== 策略配置 ====================
                .strategyConfig(builder -> {
                    builder.addInclude(TABLES)                    // 指定生成的表

                            // ==================== Entity 策略 ====================
                            .entityBuilder()
                            .enableLombok()                           // 启用 Lombok 注解（@Data 等）
                            .enableTableFieldAnnotation()             // 启用 @TableField 注解
                            .formatFileName("%s")                      // Entity 类名格式（不添加前后缀）

                            // ==================== Mapper 策略 ====================
                            .mapperBuilder()
                            .formatMapperFileName("%sMapper")          // Mapper 接口名格式
                            .formatXmlFileName("%sMapper")             // XML 文件名格式（虽不生成但配置）

                            // ==================== Service 策略 ====================
                            .serviceBuilder()
                            .formatServiceFileName("%sService")        // Service 接口名格式
                            .formatServiceImplFileName("%sServiceImpl") // ServiceImpl 类名格式

                            // ==================== Controller 策略 ====================
                            .controllerBuilder()
                            .enableRestStyle()                         // 生成 @RestController（而非 @Controller）
                            .formatFileName("%sController");            // Controller 类名格式
                })

                // ==================== 模板配置（禁用 XML 生成） ====================
                .templateConfig(builder -> {
                    builder.disable(TemplateType.XML);  // 不生成 XML 文件，使用注解方式
                })

                // ==================== 模板引擎 ====================
                .templateEngine(new FreemarkerTemplateEngine())

                // ==================== 执行生成 ====================
                .execute();

        System.out.println("==============================================");
        System.out.println("  代码生成完成！");
        System.out.println("  生成路径: " + JAVA_OUTPUT_PATH + "/com/shouyang");
        System.out.println("  包含模块: entity / mapper / service / service.impl / controller");
        System.out.println("==============================================");
    }
}
