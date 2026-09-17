# 寿阳文旅云 - 文化旅游服务平台

> 计算机科学与技术专业本科毕业设计项目
> 基于 Spring Boot + Vue3 的全栈文化旅游服务平台

---

## 项目简介

寿阳文旅云是一个面向地方文化旅游推广的综合性服务平台，旨在为用户提供资讯浏览、景点查询、活动报名、非遗展示、文创购物等一站式文化旅游服务。

项目采用前后端分离架构，后端基于 Spring Boot + MyBatis-Plus + MySQL，前端基于 Vue3 + Element Plus + Pinia，支持前台用户端和后台管理端。

**项目定位：** 本科毕业设计（合格线导向，适当简化规避风险）

---

## 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| JWT (jjwt) | 0.11.5 | 身份认证 |
| Hutool | 5.8.22 | 工具类库 |
| Knife4j | 4.3.0 | 接口文档 |
| Lombok | 1.18.30 | 简化代码 |
| Validation | - | 参数校验 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| Element Plus | 最新 | UI 组件库 |
| Axios | 最新 | HTTP 请求库 |
| Vite | 5.x | 构建工具 |
| ECharts | 5.x | 图表库 |
| wangEditor | 5.x | 富文本编辑器 |
| SCSS | - | CSS 预处理器 |

---

## 项目结构

```
Shouyang/
├── shouyang-server/          # 后端工程
│   ├── src/main/java/com/shouyang/
│   │   ├── ShouyangApplication.java    # 启动类
│   │   ├── common/                     # 公共模块
│   │   │   ├── result/                 # 统一响应结果
│   │   │   └── exception/              # 异常处理
│   │   ├── config/                     # 配置类
│   │   │   ├── MybatisPlusConfig.java # MyBatis-Plus配置
│   │   │   ├── CorsConfig.java         # 跨域配置
│   │   │   ├── Knife4jConfig.java      # 接口文档配置
│   │   │   ├── WebMvcConfig.java       # Web配置（静态资源/拦截器）
│   │   │   └── DataInitializer.java    # 数据初始化
│   │   ├── controller/                 # 控制器
│   │   │   ├── api/                    # 前台接口
│   │   │   └── admin/                  # 后台接口
│   │   ├── service/                    # 服务层
│   │   │   └── impl/                   # 服务实现
│   │   ├── mapper/                     # 数据访问层
│   │   ├── entity/                     # 实体类
│   │   ├── dto/                        # 数据传输对象
│   │   ├── vo/                         # 视图对象
│   │   ├── interceptor/                # 拦截器
│   │   │   └── JwtInterceptor.java     # JWT拦截器
│   │   ├── utils/                      # 工具类
│   │   │   ├── JwtUtils.java           # JWT工具
│   │   │   ├── UserContext.java        # 用户上下文
│   │   │   └── FileUploadUtils.java    # 文件上传工具
│   │   └── generator/                  # 代码生成器
│   │       └── CodeGenerator.java       # MyBatis-Plus代码生成器
│   ├── src/main/resources/
│   │   ├── application.yml              # 主配置
│   │   ├── application-dev.yml          # 开发环境配置
│   │   └── application-prod.yml         # 生产环境配置
│   └── pom.xml                          # Maven配置
│
├── shouyang-web/             # 前台用户端工程
│   ├── src/
│   │   ├── api/                        # 接口封装
│   │   ├── assets/                     # 静态资源
│   │   ├── components/                 # 公共组件
│   │   │   ├── Header.vue               # 顶部导航
│   │   │   ├── Footer.vue               # 页脚
│   │   │   ├── Banner.vue               # 轮播图
│   │   │   ├── ItemCard.vue             # 通用内容卡片
│   │   │   ├── NewsCard.vue             # 资讯卡片
│   │   │   ├── Pagination.vue           # 分页组件
│   │   │   └── FavoriteButton.vue       # 收藏按钮
│   │   ├── layouts/                    # 布局
│   │   │   └── DefaultLayout.vue        # 默认布局
│   │   ├── views/                      # 页面
│   │   │   ├── Home/                    # 首页
│   │   │   ├── News/                    # 资讯模块
│   │   │   ├── Travel/                  # 畅游寿阳模块
│   │   │   ├── Service/                 # 文旅服务模块
│   │   │   ├── Culture/                 # 文旅时空模块
│   │   │   ├── User/                    # 用户中心
│   │   │   ├── Search/                  # 搜索页
│   │   │   ├── Login.vue                # 登录页
│   │   │   └── Register.vue             # 注册页
│   │   ├── router/                     # 路由
│   │   ├── stores/                     # Pinia状态
│   │   └── utils/                      # 工具
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── shouyang-admin/           # 后台管理端工程
│   ├── src/
│   │   ├── api/                        # 接口封装
│   │   ├── components/                 # 公共组件
│   │   │   ├── UploadImage.vue          # 图片上传组件
│   │   │   └── RichEditor.vue           # 富文本编辑器
│   │   ├── layout/                     # 布局
│   │   │   ├── index.vue                # 主布局
│   │   │   ├── Header.vue               # 顶部栏
│   │   │   └── Sidebar.vue              # 侧边栏
│   │   ├── views/                      # 页面
│   │   │   ├── Dashboard/               # 仪表盘
│   │   │   ├── News/                    # 资讯管理（完整示范）
│   │   │   ├── User/                    # 用户管理
│   │   │   ├── Profile/                 # 个人设置
│   │   │   ├── Login.vue                # 登录页
│   │   │   └── Placeholder.vue          # 占位页
│   │   ├── router/                     # 路由
│   │   ├── stores/                     # Pinia状态
│   │   └── utils/                      # 工具
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── docs/                       # 项目文档
│   ├── shouyang_culture_init.sql        # 数据库初始化脚本
│   ├── service_module_test_data.sql      # 文旅服务测试数据
│   ├── culture_module_test_data.sql      # 文旅时空测试数据
│   ├── 联调检查报告与测试用例清单.md     # 联调测试文档
│   └── ...
│
├── uploads/                    # 上传文件目录（运行时生成）
└── README.md                   # 项目说明文档
```

---

## 功能模块

### 前台用户端

| 模块 | 功能说明 | 状态 |
|------|---------|------|
| 首页 | 轮播图、快捷入口、文旅动态、文旅服务、文旅时空、畅游寿阳、文旅单位 | ✅ |
| 用户认证 | 注册、登录、JWT身份认证 | ✅ |
| 文旅动态 | 资讯列表、分类筛选、关键词搜索、资讯详情、热门资讯、相关推荐 | ✅ |
| 畅游寿阳 | 景点/美食/酒店/攻略的列表和详情，等级/星级筛选 | ✅ |
| 文旅服务 | 活动列表/详情/报名、我的报名、票务展示、场馆展示 | ✅ |
| 文旅时空 | 数字展馆、非遗文化（级别筛选）、文创商城（不做交易） | ✅ |
| 用户中心 | 个人信息、头像上传、我的收藏、我的报名、修改密码 | ✅ |
| 全站搜索 | 跨模块搜索、分类筛选、关键词高亮、分页 | ✅ |
| 收藏功能 | 资讯/景点/美食/酒店/活动/文创收藏与取消 | ✅ |

### 后台管理端

| 模块 | 功能说明 | 状态 |
|------|---------|------|
| 管理员认证 | 登录、JWT认证、修改密码 | ✅ |
| 仪表盘 | 统计卡片、资讯发布趋势图（ECharts）、热门资讯TOP5 | ✅ |
| 资讯管理 | 列表/搜索/分类筛选/新增/编辑/删除/状态切换/富文本/图片上传 | ✅ |
| 用户管理 | 列表/搜索/启用禁用/重置密码/删除 | ✅ |
| 轮播图管理 | 列表/新增/编辑/删除/排序/状态 | ⚠️ 后端就绪，前端待实现 |
| 分类管理 | 列表/新增/编辑/删除/排序/状态 | ⚠️ 后端就绪，前端待实现 |
| 活动管理 | 列表/新增/编辑/删除/报名记录查看 | ⚠️ 后端就绪，前端待实现 |
| 景点管理 | 列表/新增/编辑/删除/状态 | ⚠️ 后端就绪，前端待实现 |
| 美食管理 | 列表/新增/编辑/删除/状态 | ⚠️ 后端就绪，前端待实现 |
| 其他管理 | 票务/场馆/展馆/非遗/文创/酒店/攻略/单位 | ⚠️ 后端就绪，前端待实现 |
| 文件上传 | 单图/多图上传，按日期分目录存储 | ✅ |

---

## 环境要求

### 开发环境

- **JDK**：17（项目配置 java.version=1.8，兼容 JDK 17 运行）
- **Maven**：3.6+
- **Node.js**：16+（推荐 18+）
- **npm**：8+（推荐使用淘宝镜像）
- **MySQL**：8.0+
- **IDE**：IntelliJ IDEA（推荐）/ VS Code

### 端口占用

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端服务 | 8080 | Spring Boot 应用 |
| 前台用户端 | 5173 | Vite 开发服务器 |
| 后台管理端 | 5174 | Vite 开发服务器 |
| MySQL | 3306 | 数据库 |

---

## 快速开始

### 1. 克隆项目

```bash
git clone <仓库地址>
cd Shouyang
```

### 2. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库并执行初始化脚本
source D:/Code/Shouyang/docs/shouyang_culture_init.sql

# 可选：导入测试数据
source D:/Code/Shouyang/docs/service_module_test_data.sql
source D:/Code/Shouyang/docs/culture_module_test_data.sql
```

或使用命令行直接导入：

```bash
mysql -u root -p < D:/Code/Shouyang/docs/shouyang_culture_init.sql
```

### 3. 后端启动

```bash
cd shouyang-server

# 修改数据库配置（如需要）
# 编辑 src/main/resources/application-dev.yml
# 默认配置：jdbc:mysql://localhost:3306/shouyang_culture，root/123456

# Maven 编译
mvn clean compile

# 启动应用（IDEA 中直接运行 ShouyangApplication.java 也可）
mvn spring-boot:run
```

启动成功后访问：
- 接口文档：http://localhost:8080/doc.html
- 健康检查：http://localhost:8080/api/banner/list

### 4. 前台用户端启动

```bash
cd shouyang-web

# 安装依赖（推荐使用淘宝镜像）
npm install --registry=https://registry.npmmirror.com

# 启动开发服务器
npm run dev
```

启动成功后访问：http://localhost:5173

### 5. 后台管理端启动

```bash
cd shouyang-admin

# 安装依赖
npm install --registry=https://registry.npmmirror.com

# 启动开发服务器
npm run dev
```

启动成功后访问：http://localhost:5174

---

## 测试账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 超级管理员 | admin | 123456 | 后台管理系统登录 |
| 前台测试用户 | test | 123456 | 前台用户端登录（需先注册或在数据库中添加） |

> 管理员密码使用 BCrypt 加密存储，初始化脚本中已包含 admin/123456 账号。

---

## 接口文档

项目集成 Knife4j 接口文档，后端启动后访问：

- **Knife4j 文档：** http://localhost:8080/doc.html
- **Swagger 原始 JSON：** http://localhost:8080/v2/api-docs

### 接口分类

| 分类 | 路径前缀 | 说明 |
|------|---------|------|
| 前台用户认证 | /api/user | 注册、登录、用户信息、修改密码 |
| 前台资讯 | /api/news | 资讯列表、详情、分类、热门、相关 |
| 前台畅游寿阳 | /api/scenic, /api/food, /api/hotel, /api/guide | 景点、美食、酒店、攻略 |
| 前台文旅服务 | /api/activity, /api/ticket, /api/venue | 活动、票务、场馆 |
| 前台文旅时空 | /api/pavilion, /api/heritage, /api/product | 展馆、非遗、文创 |
| 前台收藏 | /api/favorite | 添加/取消/列表/检查收藏 |
| 前台搜索 | /api/search | 全站搜索 |
| 前台轮播图 | /api/banner | 轮播图列表 |
| 后台认证 | /api/admin | 管理员登录、信息、修改密码 |
| 后台仪表盘 | /api/admin/dashboard | 统计数据、趋势、热门 |
| 后台用户管理 | /api/admin/user | 用户列表、状态、重置密码、删除 |
| 后台内容管理 | /api/admin/xxx | 14个模块的CRUD接口 |
| 文件上传 | /api/admin/upload | 单图/多图上传 |

---

## 部署说明

### 生产环境架构

```
用户浏览器
    │
    ▼
Nginx（80/443端口）
    ├── /              → 前台静态资源（shouyang-web/dist）
    ├── /admin         → 后台静态资源（shouyang-admin/dist）
    ├── /api/          → 反向代理到 Spring Boot（127.0.0.1:8080）
    └── /uploads/      → 反向代理到 Spring Boot（127.0.0.1:8080）
                              │
                              ▼
                    Spring Boot（8080端口）
                              │
                              ▼
                        MySQL（3306端口）
```

### 后端打包

```bash
cd shouyang-server
mvn clean package -DskipTests
# 生成 jar 包：target/shouyang-server-1.0.0.jar
```

### 前端打包

```bash
# 前台
cd shouyang-web
npm run build
# 生成：dist/

# 后台
cd shouyang-admin
npm run build
# 生成：dist/
```

### Nginx 配置参考

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前台用户端
    location / {
        root /usr/share/nginx/shouyang-web;
        try_files $uri $uri/ /index.html;
        index index.html;
    }

    # 后台管理端
    location /admin/ {
        alias /usr/share/nginx/shouyang-admin/;
        try_files $uri $uri/ /admin/index.html;
        index index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 上传文件
    location /uploads/ {
        proxy_pass http://127.0.0.1:8080;
    }
}
```

### 后端生产环境运行

```bash
# 使用生产环境配置
java -jar shouyang-server-1.0.0.jar --spring.profiles.active=prod

# 或后台运行
nohup java -jar shouyang-server-1.0.0.jar --spring.profiles.active=prod > app.log 2>&1 &
```

---

## 开发说明

### 代码生成器

项目集成 MyBatis-Plus 代码生成器，可快速生成实体类、Mapper、Service、Controller：

```bash
# 运行代码生成器（IDEA 中直接运行 CodeGenerator.java）
# 生成路径：src/main/java/com/shouyang/
# 生成模块：entity, mapper, service, service.impl, controller
```

### 通用组件

**前台：**
- `ItemCard.vue`：通用内容卡片，支持 news/scenic/food/hotel/product 等类型
- `NewsCard.vue`：资讯专用横向卡片
- `Pagination.vue`：分页组件封装
- `FavoriteButton.vue`：通用收藏按钮

**后台：**
- `UploadImage.vue`：单图上传组件，v-model 绑定图片 URL
- `RichEditor.vue`：wangEditor 富文本编辑器，v-model 绑定 HTML

### 命名规范

- 后端包名：`com.shouyang`
- 数据库表名：下划线命名（如 `sys_user`、`news_category`）
- 后端字段：驼峰命名（如 `userName`、`createTime`）
- 前端文件：PascalCase 组件名，kebab-case 路由路径
- API 路径：RESTful 风格，`/api/模块/动作`

---

## 常见问题

### Q1：npm install 一直转圈无反应？

**A：** 国内网络问题，配置淘宝镜像后重试：

```bash
npm config set registry https://registry.npmmirror.com
npm install --no-audit --no-fund
```

### Q2：后台登录提示密码错误？

**A：** 数据库中的密码哈希可能与 Hutool BCrypt 不兼容。项目已集成 `DataInitializer`，启动后端时会自动检测并重置 admin 密码为 123456（Hutool BCrypt 哈希）。重启后端即可。

### Q3：上传的图片无法访问？

**A：** 检查以下几点：
1. 后端 `WebMvcConfig` 中 `/uploads/**` 静态资源映射是否正确
2. 上传文件目录 `uploads/` 是否存在于项目根目录
3. 前端 Vite 代理是否配置了 `/uploads` 代理到 8080
4. 图片 URL 是否为 `/uploads/yyyy/MM/xxx.jpg` 格式

### Q4：接口返回 401 未授权？

**A：** 检查以下几点：
1. 是否已登录，token 是否存入 localStorage
2. 请求头是否携带 `Authorization: Bearer <token>`
3. token 是否过期（默认有效期 24 小时）
4. 接口路径是否在 JWT 拦截器白名单中

### Q5：IDEA 中 Maven 依赖无法解析？

**A：** 检查以下几点：
1. Maven 配置是否使用阿里云镜像（`~/.m2/settings.xml`）
2. 右键 pom.xml → Maven → Reload Project 刷新依赖
3. 命令行执行 `mvn clean compile` 验证依赖是否正常
4. 如命令行编译成功但 IDEA 报错，为 IDEA 索引问题，等待索引完成即可

### Q6：富文本编辑器图片上传失败？

**A：** 检查以下几点：
1. 后端 `/api/admin/upload/image` 接口是否正常
2. wangEditor 配置的上传地址和请求头是否正确
3. 上传文件大小是否超过 5MB 限制
4. 文件格式是否为 jpg/jpeg/png/gif/webp

---

## 未来扩展方向

- [ ] 百度地图接入（景点/酒店/美食地图定位与导航）
- [ ] 天气接口接入（首页/景点页显示当地天气）
- [ ] 云服务器部署（域名备案、HTTPS、持续集成）
- [ ] 评论/留言功能
- [ ] 消息通知功能
- [ ] 数据统计与分析报表
- [ ] 移动端 APP / 小程序适配

---

## 许可证

本项目仅用于学习和毕业设计，请勿用于商业用途。

---

## 联系方式

如有问题，请通过以下方式联系：

- 项目仓库：[GitHub 地址]
- 问题反馈：[Issue 地址]

---

> **最后更新：** 2026-09-17
