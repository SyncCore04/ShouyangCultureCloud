# 寿阳文旅云 - 后端服务 (shouyang-server)

基于 Spring Boot 2.7.18 + MyBatis-Plus 的文旅云平台后端服务。

> 项目定位：计算机科学与技术专业本科毕业设计（合格线导向，适当简化规避风险）
> 当前进度：核心业务模块已全部完成，后台管理 CRUD 接口已全部就绪

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 |
| MyBatis-Plus Generator | 3.5.3.1 | 代码生成器 |
| MySQL | 8.0 | 数据库 |
| JWT (jjwt) | 0.11.5 | 身份认证 |
| Lombok | 1.18.30 | 代码简化 |
| Hutool | 5.8.22 | 工具类库（含 BCrypt 加密） |
| Knife4j | 4.3.0 | 接口文档 |
| Validation | - | 参数校验 |
| Freemarker | 2.3.32 | 代码生成器模板引擎 |

---

## 功能模块清单

### 前台用户端接口（/api/xxx）

| 模块 | Controller | 接口数 | 说明 |
|------|-----------|--------|------|
| 用户认证 | ApiUserController | 7 | 注册、登录、获取信息、更新信息、修改密码、我的报名、取消报名 |
| 轮播图 | ApiBannerController | 1 | 轮播图列表 |
| 资讯模块 | ApiNewsController | 5 | 列表、详情、分类、热门、相关推荐 |
| 景点模块 | ApiScenicController | 2 | 列表（等级筛选）、详情 |
| 美食模块 | ApiFoodController | 2 | 列表（分类筛选）、详情 |
| 酒店模块 | ApiHotelController | 2 | 列表（星级筛选）、详情 |
| 攻略模块 | ApiTravelGuideController | 2 | 列表、详情 |
| 活动模块 | ApiActivityController | 4 | 列表、详情、报名、我的报名 |
| 票务模块 | ApiTicketController | 2 | 列表、详情（仅展示，不做购买） |
| 场馆模块 | ApiVenueController | 2 | 列表、详情 |
| 数字展馆 | ApiPavilionController | 2 | 列表、详情 |
| 非遗文化 | ApiHeritageController | 2 | 列表（级别/类别筛选）、详情 |
| 文创商品 | ApiProductController | 2 | 列表（价格排序）、详情（不做交易） |
| 收藏功能 | ApiFavoriteController | 4 | 添加、取消、列表、检查是否收藏 |
| 全站搜索 | ApiSearchController | 1 | 跨模块搜索（9种类型，支持分类筛选） |

### 后台管理端接口（/api/admin/xxx）

| 模块 | Controller | 接口数 | 说明 |
|------|-----------|--------|------|
| 管理员认证 | AdminAuthController | 3 | 登录、获取信息、修改密码 |
| 仪表盘 | AdminDashboardController | 3 | 统计数据、资讯发布趋势、热门资讯TOP10 |
| 用户管理 | AdminUserController | 5 | 列表、详情、启用禁用、删除、重置密码 |
| 文件上传 | AdminFileController | 2 | 单图上传、多图上传 |
| 轮播图管理 | BannerAdminController | 5 | 标准 CRUD |
| 资讯管理 | NewsAdminController | 5 | 标准 CRUD（支持分类筛选） |
| 分类管理 | CategoryAdminController | 6 | 标准 CRUD + 获取所有分类 |
| 票务管理 | TicketAdminController | 5 | 标准 CRUD |
| 场馆管理 | VenueAdminController | 5 | 标准 CRUD |
| 活动管理 | ActivityAdminController | 6 | 标准 CRUD + 报名记录列表 |
| 展馆管理 | PavilionAdminController | 5 | 标准 CRUD |
| 非遗管理 | HeritageAdminController | 5 | 标准 CRUD |
| 文创管理 | ProductAdminController | 5 | 标准 CRUD |
| 景点管理 | ScenicAdminController | 5 | 标准 CRUD |
| 美食管理 | FoodAdminController | 5 | 标准 CRUD |
| 酒店管理 | HotelAdminController | 5 | 标准 CRUD |
| 攻略管理 | GuideAdminController | 5 | 标准 CRUD |
| 单位管理 | OrgAdminController | 5 | 标准 CRUD |

> 后台管理共 18 个模块，约 95 个接口，全部使用 MyBatis-Plus IService 通用方法实现。

---

## 快速开始

### 环境要求

- JDK 17（项目配置 java.version=1.8，兼容 JDK 17 运行）
- Maven 3.6+
- MySQL 8.0+

### 数据库初始化

#### SQL 脚本说明（位于项目根目录 `docs/` 下）

| 脚本文件 | 使用场景 | 执行时机 | 说明 |
|---------|---------|---------|------|
| `shouyang_culture_init.sql` | **首次部署必须运行** | 项目初始化时 | 创建 18 张表 + 初始数据（管理员账号、6个资讯分类、3张轮播图、5条资讯、3景点3美食3酒店、测试用户） |
| `service_module_test_data.sql` | **需要文旅服务模块测试数据时运行** | 初始化后可选运行 | 插入 6 条活动 + 5 条票务 + 5 条场馆测试数据 |
| `culture_module_test_data.sql` | **需要文旅时空模块测试数据时运行** | 初始化后可选运行 | 插入 5 个数字展馆 + 8 条非遗 + 8 件文创商品测试数据 |
| `check_admin.sql` | **排查管理员登录问题时运行** | 临时调试用 | 查询管理员账号和密码哈希，用于排查密码错误问题 |

#### 执行步骤

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 创建数据库（如已创建可跳过）
CREATE DATABASE shouyang_culture DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# 3. 退出 MySQL，执行初始化脚本（必须）
exit
mysql -u root -p shouyang_culture < D:/Code/Shouyang/docs/shouyang_culture_init.sql

# 4. 可选：导入文旅服务测试数据
mysql -u root -p shouyang_culture < D:/Code/Shouyang/docs/service_module_test_data.sql

# 5. 可选：导入文旅时空测试数据
mysql -u root -p shouyang_culture < D:/Code/Shouyang/docs/culture_module_test_data.sql
```

> **注意：** PowerShell 不支持 `<` 重定向操作符，请使用 `cmd /c "mysql -u root -p shouyang_culture < 文件路径"` 或在 CMD 中执行。

### 修改配置

编辑 `src/main/resources/application-dev.yml`，确认数据库连接信息（默认 root/123456）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shouyang_culture?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

### 启动项目

```bash
# 进入项目目录
cd shouyang-server

# 编译
mvn clean compile

# 启动（开发环境）
mvn spring-boot:run
```

或在 IDEA 中直接运行 `ShouyangApplication.java`。

### 访问地址

- 服务地址：http://localhost:8080
- 接口文档（Knife4j）：http://localhost:8080/doc.html
- 健康检查：http://localhost:8080/api/banner/list

### 测试账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 超级管理员 | admin | 123456 | 后台管理系统登录 |
| 前台测试用户 | test | 123456 | 前台用户端登录 |

> **管理员密码说明：** 初始化脚本中的密码哈希使用在线 BCrypt 生成，与 Hutool 的 BCrypt 实现可能不兼容。项目已集成 `DataInitializer`，启动时会自动检测 admin 密码，如 Hutool 校验失败则自动用 Hutool 重新生成哈希并更新数据库。如遇密码错误，重启后端服务即可自动修复。

---

## 项目结构

```
shouyang-server/
├── src/main/java/com/shouyang/
│   ├── ShouyangApplication.java        # 启动类
│   ├── common/                          # 公共模块
│   │   ├── result/                      # 统一响应结果
│   │   │   ├── Result.java              # 统一响应封装
│   │   │   └── ResultCode.java          # 结果码枚举
│   │   └── exception/                   # 异常处理
│   │       ├── BusinessException.java    # 业务异常
│   │       └── GlobalExceptionHandler.java # 全局异常处理器
│   ├── config/                          # 配置类
│   │   ├── MybatisPlusConfig.java       # MyBatis-Plus 配置（分页插件）
│   │   ├── CorsConfig.java              # 跨域配置
│   │   ├── Knife4jConfig.java           # 接口文档配置
│   │   ├── WebMvcConfig.java            # Web MVC 配置（静态资源+拦截器）
│   │   └── DataInitializer.java         # 数据初始化（启动时修复管理员密码）
│   ├── interceptor/                     # 拦截器
│   │   └── JwtInterceptor.java          # JWT 认证拦截器
│   ├── utils/                           # 工具类
│   │   ├── JwtUtils.java                # JWT 工具（生成/解析/判断过期）
│   │   ├── UserContext.java             # 当前用户上下文（从 request 取 userId）
│   │   └── FileUploadUtils.java         # 文件上传工具
│   ├── generator/                       # 代码生成器
│   │   └── CodeGenerator.java           # MyBatis-Plus 代码生成器（已运行，生成90个文件）
│   ├── entity/                          # 实体类（18个，对应18张表）
│   ├── mapper/                          # Mapper 接口（18个，继承 BaseMapper）
│   ├── service/                         # Service 接口层
│   │   └── impl/                        # Service 实现层
│   ├── controller/                      # Controller 层
│   │   ├── api/                         # 前台接口（15个 Controller）
│   │   └── admin/                       # 后台接口（18个 Controller）
│   ├── dto/                             # 数据传输对象
│   │   ├── UserLoginDTO.java
│   │   ├── UserRegisterDTO.java
│   │   ├── UserUpdateDTO.java
│   │   └── PasswordUpdateDTO.java
│   └── vo/                              # 视图对象
│       ├── SearchResultVO.java          # 搜索结果统一格式
│       ├── FavoriteVO.java              # 收藏列表视图
│       └── ActivityRegisterVO.java      # 报名记录视图
├── src/main/resources/
│   ├── application.yml                   # 主配置（环境切换）
│   ├── application-dev.yml               # 开发环境配置
│   └── application-prod.yml              # 生产环境配置
├── uploads/                              # 上传文件目录（运行时生成）
└── pom.xml
```

---

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
| 1001 | 用户名已存在 |
| 1002 | 密码错误 |
| 1003 | 用户不存在 |
| 1004 | Token 已过期 |
| 1005 | Token 无效 |
| 1006 | 未登录 |

### 认证方式

需要登录的接口在请求头中携带 Token：

```
Authorization: Bearer <token>
```

### JWT 拦截器配置

- **拦截路径：** `/api/user/**`、`/api/admin/**`、`/api/favorite/**`、`/api/activity/register`、`/api/activity/my/**`
- **白名单：** `/api/user/login`、`/api/user/register`、`/api/admin/login`
- **Token 内容：** userId、username、userType（user/admin）

### 分页格式

使用 MyBatis-Plus 的 `Page` 对象，返回格式：

```json
{
  "records": [],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```

---

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
- 单文件最大 5MB，支持 jpg/jpeg/png/gif/webp 格式
- 文件按日期分目录存储：`uploads/yyyy/MM/uuid.jpg`

### 代码生成器

运行 `com.shouyang.generator.CodeGenerator` 的 main 方法，可根据数据库表自动生成 entity、mapper、service、service.impl、controller 代码。

- 已生成 18 张表的代码，共 90 个文件
- 不生成 XML 文件（使用 MyBatis-Plus 注解方式）
- Entity 使用 Lombok 注解，字段自动添加中文注释

---

## 部署

### 本地打包

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/shouyang-server-1.0.0.jar --spring.profiles.active=prod
```

### Docker 部署

详见项目根目录 `docs/Docker部署指南.md`，支持三容器标准部署（Nginx + Spring Boot + MySQL）和两容器最简部署。

---

## 常见问题

### Q1：后台登录提示密码错误？

**A：** 数据库中的密码哈希可能与 Hutool BCrypt 不兼容。项目已集成 `DataInitializer`，**重启后端服务**会自动检测并重置 admin 密码为 123456（Hutool BCrypt 哈希）。如仍有问题，可执行 `docs/check_admin.sql` 查看当前密码哈希。

### Q2：上传的图片无法访问？

**A：** 检查以下几点：
1. 项目根目录下 `uploads/` 目录是否存在
2. `WebMvcConfig` 中 `/uploads/**` 静态资源映射是否正确
3. 前端 Vite 代理是否配置了 `/uploads` 代理到 8080
4. 图片 URL 是否为 `/uploads/yyyy/MM/xxx.jpg` 格式

### Q3：接口返回 401 未授权？

**A：** 检查以下几点：
1. 是否已登录，token 是否存入 localStorage
2. 请求头是否携带 `Authorization: Bearer <token>`
3. token 是否过期（默认有效期 24 小时）
4. 接口路径是否在 JWT 拦截器白名单中

### Q4：IDEA 中 Maven 依赖无法解析？

**A：** 检查以下几点：
1. Maven 配置是否使用阿里云镜像（`~/.m2/settings.xml`）
2. 右键 pom.xml → Maven → Reload Project 刷新依赖
3. 命令行执行 `mvn clean compile` 验证依赖是否正常
4. 如命令行编译成功但 IDEA 报错，为 IDEA 索引问题，等待索引完成即可

### Q5：npm install 一直转圈？

**A：** 国内网络问题，配置淘宝镜像后重试：
```bash
npm config set registry https://registry.npmmirror.com
npm install --no-audit --no-fund
```

---

## 关联项目

| 项目 | 路径 | 说明 |
|------|------|------|
| 前台用户端 | `../shouyang-web/` | Vue3 + Element Plus，端口 5173 |
| 后台管理端 | `../shouyang-admin/` | Vue3 + Element Plus，端口 5174 |
| 项目文档 | `../docs/` | 数据库脚本、部署指南、联调报告等 |

---

## 许可证

本项目仅用于学习和毕业设计，请勿用于商业用途。

---

> **最后更新：** 2026-09-17
