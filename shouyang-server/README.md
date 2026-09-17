# 寿阳文旅云 - 后端服务 (shouyang-server)

基于 Spring Boot 2.7.18 + MyBatis-Plus 的文旅云平台后端服务。

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| JWT (jjwt) | 0.11.5 | 身份认证 |
| Lombok | - | 代码简化 |
| Hutool | 5.8.22 | 工具类库 |
| Knife4j | 4.3.0 | 接口文档 |
| Validation | - | 参数校验 |

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 数据库准备

1. 创建数据库：

```sql
CREATE DATABASE shouyang_culture DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 修改 `src/main/resources/application-dev.yml` 中的数据库连接信息（默认 root/123456）。

### 启动项目

```bash
# 进入项目目录
cd shouyang-server

# 编译
mvn clean compile

# 启动（开发环境）
mvn spring-boot:run
```

### 访问地址

- 服务地址：http://localhost:8080
- 接口文档：http://localhost:8080/doc.html

## 项目结构

```
shouyang-server/
├── src/main/java/com/shouyang/
│   ├── ShouyangApplication.java    # 启动类
│   ├── common/                      # 公共模块
│   │   ├── result/                  # 统一响应结果
│   │   └── exception/               # 异常处理
│   ├── config/                      # 配置类
│   │   ├── MybatisPlusConfig.java   # MyBatis-Plus 配置
│   │   ├── CorsConfig.java          # 跨域配置
│   │   ├── Knife4jConfig.java       # 接口文档配置
│   │   └── WebMvcConfig.java        # Web MVC 配置（静态资源+拦截器）
│   ├── interceptor/                 # 拦截器
│   │   └── JwtInterceptor.java      # JWT 认证拦截器
│   ├── utils/                       # 工具类
│   │   ├── JwtUtils.java            # JWT 工具
│   │   └── UserContext.java         # 当前用户上下文
│   ├── entity/                      # 实体类
│   ├── mapper/                      # Mapper 接口
│   ├── service/                     # Service 层
│   │   └── impl/
│   ├── controller/                  # Controller 层
│   │   ├── api/                     # 前台接口
│   │   └── admin/                   # 后台接口
│   └── dto/                         # 数据传输对象
│       ├── request/
│       └── response/
├── src/main/resources/
│   ├── application.yml               # 主配置
│   ├── application-dev.yml           # 开发环境配置
│   ├── application-prod.yml          # 生产环境配置
│   └── mapper/                       # MyBatis XML
├── uploads/                          # 上传文件目录
└── pom.xml
```

## 接口规范

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 状态码

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/Token 失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |
| 1000+ | 业务错误 |

### 认证方式

需要登录的接口在请求头中携带 Token：

```
Authorization: Bearer <token>
```

## 配置说明

### 切换环境

在 `application.yml` 中修改：

```yaml
spring:
  profiles:
    active: dev  # dev / prod
```

或启动时指定：

```bash
java -jar shouyang-server.jar --spring.profiles.active=prod
```

### 上传文件

- 上传目录：项目根目录下 `uploads/`（可在配置中修改 `file.upload-path`）
- 访问路径：`http://localhost:8080/uploads/xxx.jpg`
- 单文件最大 10MB，单次请求最大 50MB

## 部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/shouyang-server.jar --spring.profiles.active=prod
```
