# 寿阳文旅云 — 本科毕设复刻项目实施方案与 Vibe Coding 提示词

> 项目定位：计算机科学与技术本科毕业设计（合格线导向，不追求优秀）
> 复刻目标：https://sywly.chaoxing.com/ （寿阳文旅云平台）
> 技术栈：SpringBoot + Vue3 + MySQL
> 当前阶段：搭建主要逻辑框架，预留扩展接口

---

## 第一部分：实施方案

### 一、项目概述

#### 1.1 项目背景
寿阳文旅云是山西省晋中市寿阳县的公共文旅服务平台，由超星集团提供技术支持。平台集文旅资讯、在线服务、数字文化资源、旅游指南于一体，是地方文旅数字化的典型案例。

本项目旨在用 SpringBoot + Vue 技术栈复刻该网站的核心功能与界面，并在视觉样式上进行现代化升级，作为本科毕业设计交付。

#### 1.2 项目目标
- ✅ 复刻原站 6 大核心板块的主要功能
- ✅ 实现前后端分离架构，接口 RESTful 化
- ✅ 提供后台管理系统，支持内容增删改查
- ✅ 实现用户注册登录、收藏、活动报名等交互
- ✅ 视觉样式现代化升级（Element Plus + 自定义主题）
- ✅ 代码结构清晰，文档齐全，可正常运行演示

#### 1.3 非目标（明确不做，规避风险）
- ❌ 不做真实在线支付（票务/文创仅展示+模拟下单）
- ❌ 不做复杂权限系统（仅管理员/普通用户两级）
- ❌ 不做文件上传到云存储（本地存储即可）
- ❌ 不做高并发优化（单实例部署，满足演示）
- ❌ 不做移动端 APP（响应式网页即可）
- ❌ 不做全文检索（MySQL LIKE 查询替代）
- ❌ 不做消息推送/短信验证（模拟即可）

---

### 二、技术栈选型

#### 2.1 后端技术栈

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|---------|
| Spring Boot | 2.7.18 | 核心框架 | 稳定成熟，资料多，毕设首选 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 | 简化 CRUD，代码生成器好用 |
| MySQL | 8.0.x | 数据库 | 最流行，部署简单 |
| JWT | 0.11.5 | 身份认证 | 无状态，前后端分离标配 |
| Lombok | 1.18.30 | 代码简化 | 减少样板代码 |
| Hutool | 5.8.22 | 工具类库 | 中文文档好，功能全面 |
| Validation | 2.7.18 | 参数校验 | Spring Boot 自带 |
| Knife4j | 4.3.0 | 接口文档 | Swagger 增强，界面好看 |

> **不引入 Redis**：毕设演示量级不需要，减少部署复杂度和出错概率。如果后期需要缓存再手动加。

#### 2.2 前端技术栈

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|---------|
| Vue | 3.4.x | 核心框架 | 组合式 API，生态成熟 |
| Vite | 5.x | 构建工具 | 启动快，热更新快 |
| Element Plus | 2.5.x | UI 组件库 | 中文文档好，组件齐全 |
| Vue Router | 4.x | 路由管理 | 官方路由 |
| Pinia | 2.x | 状态管理 | Vue3 官方推荐，比 Vuex 简单 |
| Axios | 1.6.x | HTTP 客户端 | 事实标准 |
| ECharts | 5.4.x | 图表库 | 后台数据统计用 |
| Sass | 1.70.x | CSS 预处理器 | 样式管理 |

#### 2.3 开发工具
- IDE：IntelliJ IDEA（后端）+ VS Code（前端）
- 数据库工具：Navicat / DBeaver / IDEA 自带 Database
- 接口测试：Apifox / Postman
- 版本控制：Git + Gitee（私有仓库）

---

### 三、系统架构设计

#### 3.1 整体架构

```
┌─────────────────────────────────────────────────────┐
│                     客户端层                           │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │  前台用户端   │  │  后台管理端   │  │  移动端浏览器│ │
│  │  (Vue3 SPA)  │  │  (Vue3 SPA)  │  │  (响应式适配)│ │
│  └──────┬───────┘  └──────┬───────┘  └─────┬─────┘ │
└─────────┼───────────────────┼──────────────────┼──────┘
          │                   │                  │
          └───────────────────┼──────────────────┘
                              │ HTTP/HTTPS (RESTful API)
                              ▼
┌─────────────────────────────────────────────────────┐
│                    网关/拦截器层                        │
│         JWT 认证拦截器  +  跨域配置  +  统一异常处理    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                    业务控制层 (Controller)             │
│  首页模块 │ 资讯模块 │ 服务模块 │ 时空模块 │ 旅游模块  │
│  单位模块 │ 用户模块 │ 后台管理 │ 文件上传 │ 统计分析  │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                    业务逻辑层 (Service)                │
│         业务处理  +  数据校验  +  事务管理             │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                    数据访问层 (Mapper)                 │
│              MyBatis-Plus  +  自定义 SQL              │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                    数据存储层                           │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │   MySQL 8.0  │  │  本地文件存储  │  │  (预留)Redis│ │
│  │  业务数据     │  │  图片/附件    │  │  缓存/会话  │ │
│  └──────────────┘  └──────────────┘  └───────────┘ │
└─────────────────────────────────────────────────────┘
```

#### 3.2 前后端分离说明
- 前端两个独立工程：`shouyang-web`（前台）、`shouyang-admin`（后台）
- 后端一个工程：`shouyang-server`，通过不同 URL 前缀区分前台/后台接口
  - 前台接口：`/api/`
  - 后台接口：`/api/admin/`
- 部署时前端打包后的静态文件由 Nginx 托管，后端由 Jar 包运行

---

### 四、功能模块规划（简化版）

#### 4.1 模块总览

| 模块 | 优先级 | 功能点 | 简化说明 |
|------|--------|--------|---------|
| 首页 | P0 | 轮播图、快捷入口、新闻速览、各板块推荐 | 静态配置化 |
| 文旅动态 | P0 | 资讯列表（分类筛选+分页）、资讯详情 | 核心内容模块 |
| 文旅服务 | P1 | 票务列表、场馆列表、活动列表+报名、文旅日历 | 报名仅记录，不支付 |
| 文旅时空 | P1 | 数字展馆、非遗文化、文创商城列表+详情 | 商城仅展示，不交易 |
| 畅游寿阳 | P0 | 景点、美食、酒店、攻略列表+详情 | 核心展示模块 |
| 文旅单位 | P2 | 机构名录列表+详情 | 简单列表 |
| 用户系统 | P0 | 注册、登录、个人中心、收藏、报名记录 | JWT 认证 |
| 后台管理 | P0 | 登录、仪表盘、各模块 CRUD、用户管理 | 内容管理核心 |
| 搜索功能 | P1 | 全站关键词搜索 | MySQL LIKE 多表查询 |

> **P0 必须做，P1 尽量做，P2 时间不够可省略或合并到其他模块。**

#### 4.2 前台页面清单

```
shouyang-web/
├── 首页 (/)
│   ├── 顶部导航栏（含搜索框、登录/注册入口）
│   ├── 轮播 Banner（3-5 张，后台可配置）
│   ├── 快捷入口图标区（6 大板块入口）
│   ├── 文旅动态速览（最新 6 条）
│   ├── 文旅服务入口区
│   ├── 文旅时空入口区
│   ├── 畅游寿阳推荐区（景点/美食/酒店）
│   └── 页脚（备案信息、联系方式）
│
├── 文旅动态 (/news)
│   ├── 列表页（分类标签筛选 + 分页 + 搜索）
│   └── 详情页 (/news/:id)
│
├── 文旅服务 (/service)
│   ├── 票务预订列表 (/service/ticket)
│   ├── 场馆预订列表 (/service/venue)
│   ├── 活动报名列表 (/service/activity)
│   ├── 活动详情+报名表单 (/service/activity/:id)
│   └── 文旅日历 (/service/calendar)
│
├── 文旅时空 (/culture)
│   ├── 数字展馆列表 (/culture/pavilion)
│   ├── 非遗文化列表 (/culture/heritage)
│   ├── 文创商城列表 (/culture/product)
│   └── 通用详情页 (/culture/:type/:id)
│
├── 畅游寿阳 (/travel)
│   ├── 景点推荐列表 (/travel/scenic)
│   ├── 特色美食列表 (/travel/food)
│   ├── 民宿酒店列表 (/travel/hotel)
│   ├── 旅游攻略列表 (/travel/guide)
│   └── 通用详情页 (/travel/:type/:id)
│
├── 文旅单位 (/org)
│   └── 列表+详情
│
├── 用户中心 (/user)
│   ├── 个人信息
│   ├── 我的收藏
│   ├── 我的报名
│   └── 修改密码
│
└── 登录/注册 (/login, /register)
```

#### 4.3 后台管理页面清单

```
shouyang-admin/
├── 登录页 (/login)
├── 布局框架（侧边栏 + 顶部栏 + 内容区）
├── 仪表盘 (/dashboard)
│   ├── 数据统计卡片（用户数、资讯数、报名数等）
│   ├── 资讯发布趋势图（ECharts 折线图）
│   └── 热门资讯排行
├── 轮播图管理 (/banner)
├── 资讯管理 (/news) — 列表 + 新增/编辑富文本
├── 分类管理 (/category) — 资讯分类
├── 票务管理 (/ticket)
├── 场馆管理 (/venue)
├── 活动管理 (/activity) — 含报名记录查看
├── 展馆管理 (/pavilion)
├── 非遗管理 (/heritage)
├── 文创管理 (/product)
├── 景点管理 (/scenic)
├── 美食管理 (/food)
├── 酒店管理 (/hotel)
├── 攻略管理 (/guide)
├── 单位管理 (/org)
├── 用户管理 (/user) — 前台用户列表
├── 管理员管理 (/admin) — 仅超级管理员可见
└── 个人设置 — 修改密码
```

---

### 五、数据库设计

#### 5.1 数据库概览
- 数据库名：`shouyang_culture`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_general_ci`
- 表数量：约 18 张

#### 5.2 核心表结构

**1. 用户表 `sys_user`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| username | varchar(50) | 用户名（唯一） |
| password | varchar(100) | 密码（BCrypt 加密） |
| nickname | varchar(50) | 昵称 |
| avatar | varchar(255) | 头像 URL |
| phone | varchar(20) | 手机号 |
| email | varchar(100) | 邮箱 |
| gender | tinyint | 性别 0未知 1男 2女 |
| status | tinyint | 状态 0禁用 1正常 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

**2. 管理员表 `sys_admin`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| username | varchar(50) | 用户名（唯一） |
| password | varchar(100) | 密码 |
| nickname | varchar(50) | 昵称 |
| role | varchar(20) | 角色 admin/super |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**3. 轮播图表 `banner`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| title | varchar(100) | 标题 |
| image | varchar(255) | 图片 URL |
| link_url | varchar(255) | 跳转链接 |
| sort | int | 排序（越小越靠前） |
| status | tinyint | 0禁用 1启用 |
| create_time | datetime | 创建时间 |

**4. 资讯分类表 `news_category`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(50) | 分类名称 |
| sort | int | 排序 |
| status | tinyint | 状态 |

**5. 资讯表 `news`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| title | varchar(200) | 标题 |
| category_id | bigint | 分类 ID |
| cover_image | varchar(255) | 封面图 |
| summary | varchar(500) | 摘要 |
| content | longtext | 正文（富文本 HTML） |
| author | varchar(50) | 作者/来源 |
| view_count | int | 浏览量 |
| is_top | tinyint | 是否置顶 |
| status | tinyint | 0草稿 1发布 |
| create_time | datetime | 发布时间 |
| update_time | datetime | 更新时间 |

**6. 票务表 `ticket`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 票名 |
| cover_image | varchar(255) | 封面图 |
| venue | varchar(100) | 场馆/地点 |
| price | decimal(10,2) | 价格（0=免费） |
| start_time | datetime | 开始时间 |
| end_time | datetime | 结束时间 |
| total_count | int | 总票数 |
| remain_count | int | 剩余票数 |
| description | text | 详情描述 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**7. 场馆表 `venue`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 场馆名称 |
| cover_image | varchar(255) | 封面图 |
| address | varchar(200) | 地址 |
| open_time | varchar(100) | 开放时间 |
| contact | varchar(50) | 联系电话 |
| description | text | 场馆介绍 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**8. 活动表 `activity`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| title | varchar(200) | 活动标题 |
| cover_image | varchar(255) | 封面图 |
| venue | varchar(100) | 活动地点 |
| start_time | datetime | 开始时间 |
| end_time | datetime | 结束时间 |
| signup_deadline | datetime | 报名截止 |
| max_people | int | 最大人数 |
| signup_count | int | 已报名人数 |
| description | longtext | 活动详情 |
| status | tinyint | 0未开始 1进行中 2已结束 |
| create_time | datetime | 创建时间 |

**9. 活动报名表 `activity_register`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| activity_id | bigint | 活动 ID |
| user_id | bigint | 用户 ID |
| name | varchar(50) | 报名人姓名 |
| phone | varchar(20) | 联系电话 |
| remark | varchar(200) | 备注 |
| status | tinyint | 0待审核 1已通过 2已取消 |
| create_time | datetime | 报名时间 |

**10. 数字展馆表 `pavilion`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 展馆名称 |
| cover_image | varchar(255) | 封面图 |
| description | text | 展馆介绍 |
| content | longtext | 详细内容（富文本） |
| view_count | int | 浏览量 |
| sort | int | 排序 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**11. 非遗文化表 `heritage`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 非遗名称 |
| cover_image | varchar(255) | 封面图 |
| level | varchar(20) | 级别（国家级/省级/市级/县级） |
| category | varchar(50) | 类别（传统技艺/民俗/传统音乐等） |
| inheritor | varchar(50) | 传承人 |
| description | text | 简介 |
| content | longtext | 详细内容 |
| view_count | int | 浏览量 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**12. 文创商品表 `product`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 商品名称 |
| cover_image | varchar(255) | 封面图 |
| images | text | 多图（JSON 数组） |
| price | decimal(10,2) | 价格 |
| original_price | decimal(10,2) | 原价 |
| stock | int | 库存 |
| description | text | 商品描述 |
| content | longtext | 详情 |
| view_count | int | 浏览量 |
| status | tinyint | 0下架 1上架 |
| create_time | datetime | 创建时间 |

**13. 景点表 `scenic_spot`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 景点名称 |
| cover_image | varchar(255) | 封面图 |
| level | varchar(20) | 等级（5A/4A/3A/无） |
| address | varchar(200) | 地址 |
| open_time | varchar(100) | 开放时间 |
| ticket_price | varchar(50) | 门票价格（文字描述） |
| phone | varchar(20) | 联系电话 |
| description | text | 简介 |
| content | longtext | 详细介绍 |
| view_count | int | 浏览量 |
| sort | int | 排序 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**14. 美食表 `food`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 美食名称 |
| cover_image | varchar(255) | 封面图 |
| category | varchar(50) | 类型（主食/小吃/菜肴等） |
| description | text | 简介 |
| content | longtext | 详细介绍（可含做法） |
| view_count | int | 浏览量 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**15. 酒店表 `hotel`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 酒店名称 |
| cover_image | varchar(255) | 封面图 |
| star | int | 星级（1-5） |
| address | varchar(200) | 地址 |
| phone | varchar(20) | 联系电话 |
| price_range | varchar(50) | 价格区间 |
| description | text | 简介 |
| content | longtext | 详细介绍 |
| view_count | int | 浏览量 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**16. 旅游攻略表 `travel_guide`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| title | varchar(200) | 攻略标题 |
| cover_image | varchar(255) | 封面图 |
| author | varchar(50) | 作者/来源 |
| days | varchar(20) | 游玩天数 |
| route | varchar(200) | 路线概要 |
| description | text | 简介 |
| content | longtext | 详细攻略 |
| view_count | int | 浏览量 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**17. 文旅单位表 `organization`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| name | varchar(100) | 单位名称 |
| cover_image | varchar(255) | 封面图 |
| type | varchar(50) | 类型（文化馆/图书馆/分馆等） |
| address | varchar(200) | 地址 |
| phone | varchar(20) | 联系电话 |
| description | text | 简介 |
| content | longtext | 详细介绍 |
| sort | int | 排序 |
| status | tinyint | 状态 |
| create_time | datetime | 创建时间 |

**18. 收藏表 `favorite`**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 主键 |
| user_id | bigint | 用户 ID |
| target_type | varchar(30) | 收藏类型（news/scenic/food/hotel/activity/product 等） |
| target_id | bigint | 目标 ID |
| create_time | datetime | 收藏时间 |

> 索引建议：各表 `status`、`create_time` 建普通索引；`news` 的 `category_id` 建索引；`favorite` 的 `(user_id, target_type, target_id)` 建唯一索引防止重复收藏。

---

### 六、接口设计规范

#### 6.1 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/Token 失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

#### 6.2 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

#### 6.3 接口清单（核心）

**前台接口 `/api`**

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/banner/list | 轮播图列表 |
| GET | /api/news/list | 资讯列表（分页+分类筛选） |
| GET | /api/news/{id} | 资讯详情 |
| GET | /api/news/category | 资讯分类列表 |
| GET | /api/ticket/list | 票务列表 |
| GET | /api/ticket/{id} | 票务详情 |
| GET | /api/venue/list | 场馆列表 |
| GET | /api/venue/{id} | 场馆详情 |
| GET | /api/activity/list | 活动列表 |
| GET | /api/activity/{id} | 活动详情 |
| POST | /api/activity/register | 活动报名（需登录） |
| GET | /api/pavilion/list | 展馆列表 |
| GET | /api/pavilion/{id} | 展馆详情 |
| GET | /api/heritage/list | 非遗列表 |
| GET | /api/heritage/{id} | 非遗详情 |
| GET | /api/product/list | 文创列表 |
| GET | /api/product/{id} | 文创详情 |
| GET | /api/scenic/list | 景点列表 |
| GET | /api/scenic/{id} | 景点详情 |
| GET | /api/food/list | 美食列表 |
| GET | /api/food/{id} | 美食详情 |
| GET | /api/hotel/list | 酒店列表 |
| GET | /api/hotel/{id} | 酒店详情 |
| GET | /api/guide/list | 攻略列表 |
| GET | /api/guide/{id} | 攻略详情 |
| GET | /api/org/list | 单位列表 |
| GET | /api/org/{id} | 单位详情 |
| GET | /api/search | 全站搜索 |
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/info | 获取当前用户信息（需登录） |
| PUT | /api/user/info | 更新用户信息（需登录） |
| PUT | /api/user/password | 修改密码（需登录） |
| POST | /api/favorite/add | 添加收藏（需登录） |
| DELETE | /api/favorite/delete | 取消收藏（需登录） |
| GET | /api/favorite/list | 我的收藏列表（需登录） |
| GET | /api/user/activity/list | 我的报名列表（需登录） |

**后台接口 `/api/admin`**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/admin/login | 管理员登录 |
| GET | /api/admin/dashboard/stats | 仪表盘统计数据 |
| GET | /api/admin/dashboard/trend | 资讯发布趋势 |
| GET/POST/PUT/DELETE | /api/admin/banner | 轮播图 CRUD |
| GET/POST/PUT/DELETE | /api/admin/news | 资讯 CRUD |
| GET/POST/PUT/DELETE | /api/admin/category | 分类 CRUD |
| GET/POST/PUT/DELETE | /api/admin/ticket | 票务 CRUD |
| GET/POST/PUT/DELETE | /api/admin/venue | 场馆 CRUD |
| GET/POST/PUT/DELETE | /api/admin/activity | 活动 CRUD |
| GET | /api/admin/activity/register/list | 活动报名记录 |
| GET/POST/PUT/DELETE | /api/admin/pavilion | 展馆 CRUD |
| GET/POST/PUT/DELETE | /api/admin/heritage | 非遗 CRUD |
| GET/POST/PUT/DELETE | /api/admin/product | 文创 CRUD |
| GET/POST/PUT/DELETE | /api/admin/scenic | 景点 CRUD |
| GET/POST/PUT/DELETE | /api/admin/food | 美食 CRUD |
| GET/POST/PUT/DELETE | /api/admin/hotel | 酒店 CRUD |
| GET/POST/PUT/DELETE | /api/admin/guide | 攻略 CRUD |
| GET/POST/PUT/DELETE | /api/admin/org | 单位 CRUD |
| GET/PUT/DELETE | /api/admin/user | 前台用户管理 |
| POST | /api/admin/upload/image | 图片上传 |

---

### 七、项目目录结构

#### 7.1 整体目录

```
D:\Code\Shouyang\
├── shouyang-server/          # 后端 SpringBoot 工程
├── shouyang-web/             # 前台 Vue3 工程
├── shouyang-admin/           # 后台 Vue3 工程
├── docs/                      # 项目文档
│   ├── 数据库设计.sql
│   ├── 接口文档.md
│   └── 部署说明.md
└── 寿阳文旅云-毕设实施方案与VibeCoding提示词.md
```

#### 7.2 后端目录 `shouyang-server`

```
shouyang-server/
├── src/
│   └── main/
│       ├── java/com/shouyang/
│       │   ├── ShouyangApplication.java      # 启动类
│       │   ├── common/                        # 公共模块
│       │   │   ├── result/                    # 统一响应结果
│       │   │   │   ├── Result.java
│       │   │   │   └── ResultCode.java
│       │   │   ├── exception/                 # 异常处理
│       │   │   │   ├── BusinessException.java
│       │   │   │   └── GlobalExceptionHandler.java
│       │   │   └── constant/                  # 常量
│       │   ├── config/                        # 配置类
│       │   │   ├── MybatisPlusConfig.java
│       │   │   ├── CorsConfig.java
│       │   │   ├── Knife4jConfig.java
│       │   │   └── WebMvcConfig.java
│       │   ├── interceptor/                   # 拦截器
│       │   │   └── JwtInterceptor.java
│       │   ├── utils/                         # 工具类
│       │   │   ├── JwtUtils.java
│       │   │   └── FileUploadUtils.java
│       │   ├── entity/                        # 实体类（18个）
│       │   ├── mapper/                        # Mapper 接口
│       │   ├── service/                       # Service 层
│       │   │   ├── impl/                      # Service 实现
│       │   ├── controller/                    # Controller 层
│       │   │   ├── api/                       # 前台接口
│       │   │   └── admin/                     # 后台接口
│       │   └── dto/                           # 数据传输对象
│       │       ├── request/
│       │       └── response/
│       └── resources/
│           ├── application.yml                # 主配置
│           ├── application-dev.yml            # 开发环境
│           ├── application-prod.yml           # 生产环境
│           └── mapper/                        # MyBatis XML（可选）
├── uploads/                                    # 上传文件目录（运行时创建）
└── pom.xml
```

#### 7.3 前台目录 `shouyang-web`

```
shouyang-web/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                    # 接口请求
│   │   ├── index.js            # axios 封装
│   │   ├── news.js
│   │   ├── service.js
│   │   ├── culture.js
│   │   ├── travel.js
│   │   └── user.js
│   ├── assets/                 # 静态资源
│   │   ├── images/
│   │   └── styles/
│   │       ├── index.scss      # 全局样式
│   │       └── variables.scss  # SCSS 变量
│   ├── components/             # 公共组件
│   │   ├── Header.vue          # 顶部导航
│   │   ├── Footer.vue          # 页脚
│   │   ├── Banner.vue          # 轮播图
│   │   ├── NewsCard.vue        # 资讯卡片
│   │   ├── ItemCard.vue        # 通用卡片
│   │   └── Pagination.vue      # 分页组件
│   ├── layouts/                # 布局
│   │   └── DefaultLayout.vue
│   ├── router/                 # 路由
│   │   └── index.js
│   ├── stores/                 # Pinia 状态
│   │   └── user.js
│   ├── utils/                  # 工具函数
│   │   ├── request.js          # axios 实例
│   │   └── auth.js             # token 管理
│   ├── views/                  # 页面
│   │   ├── Home/
│   │   ├── News/
│   │   ├── Service/
│   │   ├── Culture/
│   │   ├── Travel/
│   │   ├── Org/
│   │   ├── User/
│   │   ├── Login.vue
│   │   └── Register.vue
│   ├── App.vue
│   └── main.js
├── .env.development
├── .env.production
├── vite.config.js
└── package.json
```

#### 7.4 后台目录 `shouyang-admin`

```
shouyang-admin/
├── src/
│   ├── api/
│   ├── assets/
│   ├── components/
│   │   ├── UploadImage.vue     # 图片上传组件
│   │   └── RichEditor.vue      # 富文本编辑器
│   ├── layout/
│   │   ├── index.vue           # 布局主组件
│   │   ├── Sidebar.vue         # 侧边栏
│   │   └── Header.vue          # 顶部栏
│   ├── router/
│   ├── stores/
│   ├── utils/
│   ├── views/
│   │   ├── Login.vue
│   │   ├── Dashboard/
│   │   ├── Banner/
│   │   ├── News/
│   │   ├── Category/
│   │   ├── Ticket/
│   │   ├── Venue/
│   │   ├── Activity/
│   │   ├── Pavilion/
│   │   ├── Heritage/
│   │   ├── Product/
│   │   ├── Scenic/
│   │   ├── Food/
│   │   ├── Hotel/
│   │   ├── Guide/
│   │   ├── Org/
│   │   ├── User/
│   │   └── Profile/
│   ├── App.vue
│   └── main.js
├── vite.config.js
└── package.json
```

---

### 八、开发计划与里程碑

> 总工期建议：8-10 周（按每周 15-20 小时投入估算）

| 阶段 | 时间 | 交付物 | 验收标准 |
|------|------|--------|---------|
| **阶段一：准备与初始化** | 第 1 周 | 项目骨架、数据库 | 三个工程能启动，数据库表创建完成 |
| **阶段二：后端核心框架** | 第 2 周 | 后端基础架构 | 统一响应、JWT 认证、CRUD 代码生成、接口文档可访问 |
| **阶段三：前台框架+首页** | 第 3 周 | 前台基础+首页 | 前台能运行，首页完整展示（轮播+导航+各板块入口） |
| **阶段四：核心内容模块** | 第 4-5 周 | 资讯+旅游模块 | 文旅动态列表/详情、畅游寿阳四个子模块列表/详情 |
| **阶段五：服务+时空模块** | 第 6 周 | 服务+时空模块 | 文旅服务（活动报名）、文旅时空（展馆/非遗/文创） |
| **阶段六：用户系统** | 第 7 周 | 用户功能 | 注册登录、个人中心、收藏、报名记录 |
| **阶段七：后台管理系统** | 第 8 周 | 后台全部功能 | 管理员登录、仪表盘、各模块 CRUD、图片上传 |
| **阶段八：联调优化+文档** | 第 9-10 周 | 完整可运行项目 | 前后端联调通过、Bug 修复、毕设论文、演示 PPT |

---

### 九、风险规避与简化策略

#### 9.1 功能简化清单

| 原站功能 | 简化方案 | 理由 |
|---------|---------|------|
| 在线支付购票 | 仅展示票务信息，报名/下单只记录不支付 | 支付接口申请复杂，有资金风险 |
| 文创商城交易 | 商品展示页，不做购物车和订单 | 同上 |
| 真实地图集成 | 景点地址文字展示，预留地图接口位置 | 百度地图 AK 申请可后期加 |
| 天气接口 | 不做，或后期接入免费天气 API | 非核心功能 |
| 全文搜索引擎 | MySQL LIKE 多表联合查询 | 演示量级足够，减少技术复杂度 |
| 文件云存储 | 本地文件存储 + 静态资源映射 | 减少云服务依赖 |
| 短信验证 | 注册不验证手机号，或前端格式校验即可 | 短信服务需付费 |
| 复杂权限 | 仅管理员/普通用户两级，不做角色权限表 | 毕设不需要 RBAC |
| 数据统计复杂 | 仪表盘仅做简单计数和趋势图 | 减少后端统计 SQL 复杂度 |
| 富文本高级功能 | 使用 wangEditor 或 Element Plus 自带，图片本地上传 | 不接入第三方富文本云服务 |

#### 9.2 技术风险规避

1. **MyBatis-Plus 代码生成器**：用代码生成器一键生成 Entity/Mapper/Service/Controller，减少手写代码量和出错概率
2. **统一异常处理**：全局捕获异常，避免堆栈信息暴露给前端
3. **密码加密**：使用 BCrypt（Spring Security 自带，不引入完整 Security）
4. **SQL 注入防护**：MyBatis-Plus 参数化查询，禁止字符串拼接 SQL
5. **XSS 防护**：富文本内容存储时做简单过滤，展示时用 `v-html` 但内容来自后台
6. **跨域处理**：后端配置 CorsConfig，开发环境也可用 Vite proxy
7. **Token 过期**：前端 axios 拦截器统一处理 401，跳转登录页

#### 9.3 答辩应对策略
- 准备 3-5 分钟演示视频，展示核心功能流程
- 重点讲：前后端分离架构、数据库设计、核心模块实现
- 被问到没做的功能：回答"由于时间有限，本项目聚焦核心功能，XX 模块已预留扩展接口，未来可继续开发"
- 代码量：后端约 5000-8000 行，前端约 6000-10000 行，足够毕设体量

---

### 十、未来扩展方向（毕设后可继续）

1. **百度地图集成**：景点/酒店/场馆位置地图标注，路径规划
2. **天气接口**：首页展示寿阳当地天气，旅游建议
3. **云服务器部署**：阿里云/腾讯云 + Nginx + Docker，域名备案
4. **Redis 缓存**：热点数据缓存，减轻数据库压力
5. **Elasticsearch**：全文检索替代 LIKE 查询
6. **WebSocket**：消息通知、实时聊天
7. **小程序端**：微信小程序版本
8. **数据大屏**：文旅数据可视化大屏（ECharts）
9. **评论系统**：资讯/景点评论与评分
10. **推荐算法**：基于浏览记录的个性化推荐

---

## 第二部分：Vibe Coding 提示词集

> 使用说明：以下提示词按开发阶段排列，每个提示词可直接复制到 AI 编程工具（如豆包、Cursor、Claude、Windsurf 等）中使用。使用时根据实际情况微调。

### 阶段一：项目初始化

#### 提示词 1.1：创建后端 SpringBoot 项目骨架

```
请帮我创建一个 Spring Boot 2.7.18 项目骨架，项目名为 shouyang-server，包名 com.shouyang。

技术栈要求：
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- MySQL 8.0 驱动
- JWT (jjwt 0.11.5)
- Lombok
- Hutool 5.8.22
- Knife4j 4.3.0 (接口文档)
- Validation

请生成完整的 pom.xml，并创建以下基础目录和文件：
1. 启动类 ShouyangApplication.java
2. 统一响应结果类 Result.java（含 code/message/data，静态成功失败方法）
3. 结果码枚举 ResultCode.java
4. 全局异常处理器 GlobalExceptionHandler.java
5. 业务异常类 BusinessException.java
6. MyBatis-Plus 配置类（分页插件）
7. 跨域配置 CorsConfig.java
8. Knife4j 配置类
9. WebMvcConfig（静态资源映射，/uploads/** 映射到本地 uploads 目录）
10. application.yml（含 dev/prod 多环境配置，端口 8080）
11. JWT 工具类 JwtUtils.java（生成 token、解析 token、判断过期）
12. JWT 拦截器 JwtInterceptor.java（拦截 /api/** 下需要登录的接口，白名单放行登录注册）

要求：
- 代码注释用中文
- 所有类要有完整的包声明和 import
- application.yml 中数据库配置用占位符，默认 root/123456
- 上传文件目录配置为项目根目录下的 uploads 文件夹
```

#### 提示词 1.2：创建数据库初始化 SQL

```
请根据以下表结构设计，生成完整的 MySQL 8.0 数据库初始化 SQL 文件。

数据库名：shouyang_culture
字符集：utf8mb4
排序规则：utf8mb4_general_ci

需要创建以下 18 张表：
1. sys_user（用户表）
2. sys_admin（管理员表）
3. banner（轮播图表）
4. news_category（资讯分类表）
5. news（资讯表）
6. ticket（票务表）
7. venue（场馆表）
8. activity（活动表）
9. activity_register（活动报名表）
10. pavilion（数字展馆表）
11. heritage（非遗文化表）
12. product（文创商品表）
13. scenic_spot（景点表）
14. food（美食表）
15. hotel（酒店表）
16. travel_guide（旅游攻略表）
17. organization（文旅单位表）
18. favorite（收藏表）

表结构要求：
- 所有表主键为 id，bigint 类型，自增
- 所有表包含 create_time（datetime，默认当前时间）和 update_time（datetime，默认当前时间 ON UPDATE CURRENT_TIMESTAMP），除了关联表
- 状态字段用 tinyint，默认 1
- 价格字段用 decimal(10,2)
- 长文本用 longtext
- 浏览量字段 view_count 默认 0
- 为常用查询字段建索引（status, create_time, category_id, user_id 等）
- favorite 表的 (user_id, target_type, target_id) 建唯一索引

最后插入初始数据：
- 一个超级管理员账号 admin / 123456（密码用 BCrypt 加密后的字符串，$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIu 对应 123456）
- 6 个资讯分类（文旅资讯、非遗文化、活动预告、通知公告、文化惠民、旅游美文）
- 3 张轮播图（用占位图 URL）
- 5 条示例资讯数据
- 3 个景点、3 个美食、3 个酒店示例数据

请输出完整的 SQL 文件内容。
```

#### 提示词 1.3：创建前台 Vue3 项目骨架

```
请帮我规划一个 Vue 3 + Vite 前台项目骨架，项目名为 shouyang-web。

技术栈：
- Vue 3.4 + Vite 5
- Element Plus 2.5
- Vue Router 4
- Pinia 2
- Axios
- Sass

请生成以下文件的完整内容：

1. package.json（包含所有依赖和脚本）
2. vite.config.js（配置端口 5173，代理 /api 到 http://localhost:8080，路径别名 @ 指向 src）
3. .env.development 和 .env.production（API 基础地址配置）
4. src/main.js（引入 Element Plus、Pinia、Router、全局样式）
5. src/App.vue
6. src/utils/request.js（axios 封装：baseURL、超时、请求拦截器带 token、响应拦截器统一处理 code 和错误提示、401 跳转登录）
7. src/utils/auth.js（token 的 get/set/remove 方法，存在 localStorage）
8. src/stores/user.js（Pinia：用户信息、token、登录/登出/获取用户信息方法）
9. src/router/index.js（路由配置，包含路由守卫：需要登录的路由检查 token）
10. src/assets/styles/index.scss（全局样式重置、Element Plus 主题色变量覆盖，主色调用 #2c3e6b 深蓝）
11. src/assets/styles/variables.scss（SCSS 变量：主色、辅助色、文字色、背景色、圆角、阴影等）
12. index.html（标题：寿阳文旅云）

要求：
- 使用组合式 API（setup 语法糖）
- 代码注释用中文
- 路由采用懒加载
- 先只创建基础路由（首页、登录、注册），其他页面路由留 TODO 注释
- 主题色参考原站但更现代化，主色 #2c3e6b，辅助色 #e57373（文旅红）、#66bb6a（旅游绿）
```

#### 提示词 1.4：创建后台 Vue3 项目骨架

```
请帮我规划一个 Vue 3 + Vite 后台管理项目骨架，项目名为 shouyang-admin。

技术栈：
- Vue 3.4 + Vite 5
- Element Plus 2.5
- Vue Router 4
- Pinia 2
- Axios
- ECharts 5.4
- Sass
- wangEditor（富文本编辑器）

请生成以下文件的完整内容：

1. package.json
2. vite.config.js（端口 5174，代理 /api 到 http://localhost:8080）
3. src/main.js
4. src/App.vue
5. src/utils/request.js（axios 封装，同前台但 baseURL 不同）
6. src/stores/user.js（管理员状态管理）
7. src/router/index.js（路由配置 + 路由守卫，动态路由先静态写死）
8. src/layout/index.vue（后台布局：左侧可折叠菜单 + 顶部栏（面包屑、用户信息下拉） + 主内容区 router-view）
9. src/layout/Sidebar.vue（侧边栏菜单，用 el-menu，包含所有管理模块菜单项）
10. src/layout/Header.vue（顶部栏）
11. src/assets/styles/index.scss（后台全局样式，主题色 #409eff 或自定义）
12. src/views/Login.vue（管理员登录页，居中卡片表单）
13. src/views/Dashboard/index.vue（仪表盘：4 个统计卡片 + 一个 ECharts 折线图占位）

要求：
- 组合式 API
- 侧边栏菜单包含：仪表盘、轮播图管理、资讯管理、分类管理、票务管理、场馆管理、活动管理、展馆管理、非遗管理、文创管理、景点管理、美食管理、酒店管理、攻略管理、单位管理、用户管理、个人设置
- 菜单用图标（Element Plus 图标）
- 登录成功后跳转到 /dashboard
- 路由守卫检查 token，无 token 跳转 /login
```

---

### 阶段二：后端核心框架

#### 提示词 2.1：用 MyBatis-Plus 代码生成器生成所有模块代码

```
请帮我编写一个 MyBatis-Plus 代码生成器类 CodeGenerator.java，放在 com.shouyang.generator 包下。

要求：
- 数据库连接：jdbc:mysql://localhost:3306/shouyang_culture?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
- 用户名 root，密码 123456
- 生成以下 18 张表的代码：
  sys_user, sys_admin, banner, news_category, news, ticket, venue, activity,
  activity_register, pavilion, heritage, product, scenic_spot, food, hotel,
  travel_guide, organization, favorite
- 表前缀去除：无特殊前缀
- 生成包路径：com.shouyang
- 生成模块：entity, mapper, service, service.impl, controller
- Entity 用 Lombok 注解（@Data, @TableName, @TableId, @TableField）
- Entity 字段添加中文注释（从数据库注释获取）
- Mapper 接口继承 BaseMapper
- Service 接口继承 IService
- ServiceImpl 继承 ServiceImpl
- Controller 先不生成方法（只生成类框架），或生成基础的 list/page/save/update/remove 方法
- 生成路径为项目 src/main/java 下
- 不生成 XML 文件（用 MyBatis-Plus 注解方式）

请输出完整的 CodeGenerator.java 代码，并告诉我运行后需要检查哪些地方。
```

#### 提示词 2.2：实现用户认证相关接口

```
请帮我实现用户认证相关的后端接口。

需要实现：

1. 前台用户接口 ApiUserController.java（/api/user）
   - POST /api/user/register - 用户注册（参数：username, password, nickname, phone）
     - 用户名唯一校验
     - 密码 BCrypt 加密
   - POST /api/user/login - 用户登录（参数：username, password）
     - 验证密码
     - 生成 JWT token 返回
     - 返回 token 和用户基本信息
   - GET /api/user/info - 获取当前用户信息（从 token 解析 userId，查数据库返回）
   - PUT /api/user/info - 更新用户信息（nickname, avatar, phone, email, gender）
   - PUT /api/user/password - 修改密码（旧密码验证 + 新密码）

2. 管理员接口 AdminAuthController.java（/api/admin）
   - POST /api/admin/login - 管理员登录
   - GET /api/admin/info - 获取管理员信息

3. DTO 类
   - UserLoginDTO（username, password）
   - UserRegisterDTO（username, password, nickname, phone）
   - UserUpdateDTO
   - PasswordUpdateDTO（oldPassword, newPassword）

4. Service 层实现
   - UserService 接口和实现类
   - AdminService 接口和实现类

要求：
- 所有接口参数用 @Valid 校验
- 密码用 BCryptPasswordEncoder 加密（不引入 Spring Security，直接用 cn.hutool.crypto.digest.BCrypt 或自己实现 BCrypt）
- JWT token 中存储 userId 和 username
- 登录接口返回 Result<Map>，包含 token 和 userInfo
- 统一异常处理，用户名已存在、密码错误等抛 BusinessException
- 从当前请求获取用户 ID 的工具方法（从 ThreadLocal 或 request attribute 中取，在拦截器中设置）
```

#### 提示词 2.3：实现文件上传接口

```
请帮我实现文件上传功能。

需要实现：
1. AdminFileController.java（/api/admin/upload）
   - POST /api/admin/upload/image - 单张图片上传
     - 支持 jpg, jpeg, png, gif, webp 格式
     - 文件大小限制 5MB
     - 文件重命名为 UUID + 原扩展名
     - 按日期分目录存储（uploads/2024/01/）
     - 返回可访问的 URL 路径（如 /uploads/2024/01/xxx.jpg）
   - POST /api/admin/upload/images - 多张图片上传（返回 URL 列表）

2. FileUploadUtils.java 工具类
   - upload(MultipartFile file, String basePath) 方法
   - 文件类型校验
   - 文件大小校验
   - 生成唯一文件名
   - 创建目录

3. 配置
   - 在 application.yml 中配置上传路径和允许的类型/大小
   - WebMvcConfig 中已经映射 /uploads/**，确认配置正确

要求：
- 上传接口需要管理员登录（在拦截器配置中 /api/admin/** 都需要 token）
- 返回结果用统一 Result 包装
- 上传失败抛 BusinessException
- 文件名用 UUID 防止中文文件名和重名问题
```

---

### 阶段三：前台框架 + 首页

#### 提示词 3.1：实现前台公共组件（Header/Footer/Banner）

```
请帮我实现前台的公共组件。

1. Header.vue（顶部导航栏）
   - 左侧：Logo + 网站名称"寿阳文旅云"
   - 中间：主导航菜单（首页、文旅动态、文旅服务、文旅时空、畅游寿阳、文旅单位）
     - 文旅服务、文旅时空、畅游寿阳有下拉子菜单
     - 下拉菜单列出各子模块链接
   - 右侧：搜索框（点击跳转到搜索页或展开搜索）、登录/注册按钮（已登录显示用户头像+下拉菜单：个人中心、退出登录）
   - 导航项高亮当前路由
   - 固定在顶部，滚动时加背景阴影
   - 响应式：移动端显示汉堡菜单

2. Footer.vue（页脚）
   - 三列布局：关于我们、联系方式、快速链接
   - 底部：备案信息 + Copyright
   - 背景深色，文字浅色

3. Banner.vue（轮播图组件）
   - 用 el-carousel 实现
   - 从后端 /api/banner/list 获取数据
   - 图片自适应，带标题文字覆盖
   - 自动播放，3 秒切换
   - 指示器和左右箭头

4. DefaultLayout.vue
   - 包含 Header + main（router-view）+ Footer
   - 页面最小高度撑满

要求：
- 组合式 API + setup 语法糖
- 样式用 scoped SCSS
- 主色调 #2c3e6b，导航栏白底深蓝字
- 下拉菜单 hover 显示
- 搜索框用 el-input，回车跳转到 /search?keyword=xxx
- 用户状态从 Pinia user store 获取
- 退出登录调用 store 的 logout 方法并跳转首页
```

#### 提示词 3.2：实现前台首页

```
请帮我实现前台首页 Home/index.vue。

页面结构（从上到下）：

1. Banner 轮播图（用已封装的 Banner 组件）

2. 快捷入口区（6 个图标入口）
   - 文旅动态、文旅服务、文旅时空、畅游寿阳、文旅单位、活动日历
   - 每个入口：图标 + 文字，点击跳转对应页面
   - 横向排列，响应式换行

3. 文旅动态速览区
   - 左侧标题"文旅动态" + "更多"按钮
   - 右侧：新闻列表（6 条，2 列布局）
   - 每条新闻：封面图（左）+ 标题（加粗）+ 摘要（2 行省略）+ 日期
   - 从 /api/news/list 获取，按发布时间倒序

4. 文旅服务区
   - 标题"文旅服务"
   - 5 个卡片：票务预订、场馆预订、活动报名、文旅日历、志愿者
   - 每个卡片：图标 + 名称 + 简短描述
   - 点击跳转对应页面

5. 文旅时空区
   - 标题"文旅时空"
   - 4 个卡片：数字展馆、非遗文化、文创商城、云阅读
   - 卡片带背景图或图标

6. 畅游寿阳区
   - 标题"畅游寿阳"
   - Tab 切换：景点推荐、特色美食、民宿酒店
   - 每个 Tab 下显示 3 个卡片（横向排列）
   - 卡片：封面图 + 名称 + 简短信息
   - 从对应接口获取数据

7. 文旅单位区（可选，时间不够可简化）
   - 标题"文旅单位"
   - 横向滚动或网格展示单位列表

要求：
- 组合式 API
- 每个数据区域用 onMounted 调用接口
- 加载中显示 el-skeleton 骨架屏
- 图片懒加载
- 样式现代化，卡片带 hover 效果（上浮+阴影）
- 页面宽度最大 1200px 居中
- 区块之间有足够间距
```

#### 提示词 3.3：实现通用卡片组件和分页组件

```
请帮我实现两个通用组件：

1. ItemCard.vue（通用内容卡片）
   - Props: item（对象，包含 id, title/name, coverImage, description/summary, 其他可选字段）, type（news/scenic/food/hotel/product 等）
   - 布局：上方封面图（固定比例 4:3）+ 下方文字区
   - 文字区：标题（1 行省略）+ 描述（2 行省略）+ 底部元信息（日期/价格/地址等，根据 type 显示）
   - hover 效果：图片缩放 + 卡片上浮阴影
   - 点击跳转到对应详情页（根据 type 决定路由）
   - 封面图加载失败显示默认占位图

2. Pagination.vue（分页组件）
   - 封装 el-pagination
   - Props: total（总数）, page（当前页）, pageSize（每页条数，默认 10）
   - Emits: update:page（页码改变）
   - 显示：总数、页码按钮、每页条数选择
   - 布局居中

3. NewsCard.vue（资讯专用卡片，横向布局）
   - 左侧封面图（160x100）+ 右侧文字
   - 标题（1-2 行）+ 摘要（2 行）+ 分类标签 + 日期
   - hover 标题变色
   - 点击跳转资讯详情

要求：
- 组合式 API
- 样式 scoped SCSS
- 图片用 el-image，支持懒加载和预览
- 所有跳转用 router.push
- 组件要通用，通过 props 控制显示内容
```

---

### 阶段四：核心内容模块

#### 提示词 4.1：实现文旅动态模块（列表+详情）

```
请帮我实现文旅动态模块的前台页面和后端接口。

【后端】

1. ApiNewsController.java（/api/news）
   - GET /api/news/list - 资讯列表（参数：page, size, categoryId, keyword）
     - 分页查询，按 is_top 降序 + create_time 降序
     - 支持分类筛选和关键词搜索（标题 LIKE）
     - 只返回 status=1（已发布）的
     - 返回字段：id, title, categoryId, categoryName, coverImage, summary, author, viewCount, createTime
   - GET /api/news/{id} - 资讯详情
     - 查询详情，浏览量 +1
     - 返回完整内容（含 content 富文本）
   - GET /api/news/category - 分类列表（所有启用的分类）
   - GET /api/news/hot - 热门资讯（浏览量前 10）
   - GET /api/news/related/{id} - 相关资讯（同分类下的其他资讯，6 条）

2. NewsService 实现上述方法

【前端】

1. views/News/List.vue - 资讯列表页
   - 顶部：页面标题 + 分类标签筛选（横向滚动标签，全部 + 各分类）
   - 搜索框（可选，关键词搜索）
   - 列表：用 NewsCard 组件，每条一行
   - 底部分页
   - 左侧主内容区 + 右侧侧边栏（热门资讯排行、分类导航）
   - 切换分类/搜索/翻页时重新请求数据

2. views/News/Detail.vue - 资讯详情页
   - 面包屑导航
   - 标题（大字号）+ 作者/来源/发布时间/浏览量
   - 正文（富文本渲染，v-html，样式优化：图片最大宽度、段落间距、标题样式）
   - 底部：上一篇/下一篇导航
   - 侧边栏：相关资讯推荐、热门资讯
   - 收藏按钮（登录用户可收藏）

3. api/news.js - 接口封装
   - getNewsList(params)
   - getNewsDetail(id)
   - getNewsCategory()
   - getHotNews()
   - getRelatedNews(id)

要求：
- 后端分页用 MyBatis-Plus 的 Page 对象
- 浏览量 +1 用 update 语句，避免并发问题可用 set view_count = view_count + 1
- 前端富文本内容样式要单独优化（写在全局样式或 scoped 里用 :deep）
- 列表页 URL 带查询参数（?category=1&page=2），刷新后保持状态
- 详情页从路由 params 获取 id
```

#### 提示词 4.2：实现畅游寿阳模块（景点/美食/酒店/攻略）

```
请帮我实现畅游寿阳模块的前台页面和后端接口。这个模块包含 4 个子模块：景点、美食、酒店、攻略，结构类似，可以复用通用组件。

【后端 - 统一风格的 CRUD 接口】

为每个模块创建前台 Controller：
1. ApiScenicController.java（/api/scenic）- list, detail
2. ApiFoodController.java（/api/food）- list, detail
3. ApiHotelController.java（/api/hotel）- list, detail
4. ApiTravelGuideController.java（/api/guide）- list, detail

每个 list 接口支持：
- 分页参数 page, size
- 关键词搜索 keyword
- 各自的筛选条件（景点：level 等级；美食：category 类型；酒店：star 星级；攻略：无特殊筛选）
- 按 sort/create_time 排序
- 只返回 status=1 的

每个 detail 接口：
- 查询详情
- 浏览量 +1

【前端】

1. 通用列表页组件 TravelList.vue（可复用于 4 个模块，通过 props 区分类型）
   - Props: type（scenic/food/hotel/guide）, title（页面标题）
   - 顶部：标题 + 筛选条件（根据 type 显示不同筛选项）+ 搜索框
   - 内容区：网格布局的 ItemCard 卡片（每行 3-4 个）
   - 底部分页
   - 根据 type 调用不同接口

2. 通用详情页组件 TravelDetail.vue
   - Props: type
   - 顶部：封面大图 + 标题 + 基本信息（地址/电话/开放时间/价格等，根据 type 显示）
   - 正文：详细介绍（富文本）
   - 侧边栏或底部：相关推荐
   - 收藏按钮

3. 路由配置：
   - /travel/scenic -> TravelList（type=scenic）
   - /travel/food -> TravelList（type=food）
   - /travel/hotel -> TravelList（type=hotel）
   - /travel/guide -> TravelList（type=guide）
   - /travel/scenic/:id -> TravelDetail（type=scenic）
   - ... 同理

4. api/travel.js - 接口封装

要求：
- 尽量复用代码，减少重复
- 后端 4 个 Controller 结构类似，可以先写一个然后复制修改
- 前端列表页和详情页通过 type 控制显示内容和调用接口
- 卡片显示信息根据 type 不同：景点显示等级+地址，美食显示分类，酒店显示星级+价格区间，攻略显示天数+作者
- 详情页的基本信息区用 el-descriptions 组件展示
```

---

### 阶段五：服务 + 时空模块

#### 提示词 5.1：实现文旅服务模块（活动报名核心）

```
请帮我实现文旅服务模块，重点是活动报名功能。

【后端】

1. ApiActivityController.java（/api/activity）
   - GET /api/activity/list - 活动列表（分页，按时间排序，可筛选状态）
   - GET /api/activity/{id} - 活动详情
   - POST /api/activity/register - 活动报名（需登录）
     - 参数：activityId, name, phone, remark
     - 校验：活动是否存在、是否在报名时间内、是否已满、是否已报名
     - 报名成功后 signup_count +1
     - 返回报名记录 ID
   - GET /api/activity/my/list - 我的报名列表（需登录，从 token 获取 userId）

2. ApiTicketController.java（/api/ticket）- list, detail（仅展示）
3. ApiVenueController.java（/api/venue）- list, detail（仅展示）

4. ActivityRegisterService - 报名业务逻辑

【前端】

1. views/Service/ActivityList.vue - 活动列表页
   - 活动卡片列表（封面图 + 标题 + 时间 + 地点 + 报名状态标签 + 已报名/总人数进度）
   - 状态标签：未开始（蓝）、报名中（绿）、已结束（灰）
   - 分页

2. views/Service/ActivityDetail.vue - 活动详情页
   - 封面图 + 标题
   - 活动信息卡片：时间、地点、报名截止、人数限制、已报名人数（进度条）
   - 活动详情（富文本）
   - 报名按钮（根据状态显示：立即报名/报名已满/已结束/已报名）
   - 点击报名弹出报名表单对话框（el-dialog）：姓名、电话、备注
   - 报名成功提示并刷新状态

3. views/Service/TicketList.vue - 票务列表（卡片展示，价格标签，不做购买）
4. views/Service/VenueList.vue - 场馆列表（卡片展示，地址+开放时间）
5. views/Service/VenueDetail.vue - 场馆详情

6. api/service.js - 接口封装

要求：
- 报名接口需要登录，前端判断未登录时点击报名跳转到登录页（登录后回跳）
- 报名表单校验：姓名必填、手机号格式校验
- 已报名用户显示"已报名"状态，不可重复报名
- 活动状态根据当前时间和活动时间动态计算（前端计算或后端返回）
- 人数进度条用 el-progress
```

#### 提示词 5.2：实现文旅时空模块（展馆/非遗/文创）

```
请帮我实现文旅时空模块，包含数字展馆、非遗文化、文创商城三个子模块。

【后端】

1. ApiPavilionController.java（/api/pavilion）- list, detail
   - list：分页，按 sort 排序
   - detail：详情 + 浏览量 +1

2. ApiHeritageController.java（/api/heritage）- list, detail
   - list：支持按 level（级别）和 category（类别）筛选
   - detail：详情

3. ApiProductController.java（/api/product）- list, detail
   - list：支持价格排序、关键词搜索
   - detail：详情（含多图）

【前端】

1. views/Culture/PavilionList.vue - 数字展馆列表
   - 大卡片网格布局（每行 3 个）
   - 卡片：封面图 + 展馆名称 + 简介 + 浏览量
   - hover 效果

2. views/Culture/PavilionDetail.vue - 展馆详情
   - 大图展示 + 名称 + 详细介绍
   - 图片画廊（多图轮播）

3. views/Culture/HeritageList.vue - 非遗列表
   - 顶部筛选：级别（全部/国家级/省级/市级/县级）+ 类别
   - 列表卡片：封面 + 名称 + 级别标签 + 类别 + 传承人
   - 分页

4. views/Culture/HeritageDetail.vue - 非遗详情
   - 基本信息：名称、级别、类别、传承人
   - 详细介绍（富文本）

5. views/Culture/ProductList.vue - 文创商城列表
   - 商品卡片网格（每行 4 个）
   - 卡片：封面图 + 名称 + 价格（红色高亮）+ 原价（划线）
   - 排序：综合/价格升序/价格降序
   - 不做购物车，点击查看详情

6. views/Culture/ProductDetail.vue - 商品详情
   - 左侧：商品图片（主图 + 缩略图切换）
   - 右侧：名称、价格、原价、库存、描述
   - 底部：商品详情（富文本）
   - "加入购物车"和"立即购买"按钮只做提示"功能开发中"（规避支付）

7. api/culture.js - 接口封装

要求：
- 非遗级别标签用不同颜色：国家级红、省级橙、市级蓝、县级绿
- 商品价格格式化显示（¥xx.xx）
- 多图展示用 el-carousel 或 el-image-viewer
- 文创商城明确不做交易，按钮提示即可
- 列表页和详情页风格统一
```

---

### 阶段六：用户系统

#### 提示词 6.1：实现用户中心（收藏/报名记录/个人信息）

```
请帮我实现用户中心相关功能。

【后端】

1. ApiFavoriteController.java（/api/favorite）
   - POST /api/favorite/add - 添加收藏（参数：targetType, targetId）
     - 校验目标是否存在（可选简化，不校验直接存）
     - 防止重复收藏（唯一索引捕获异常）
   - DELETE /api/favorite/delete - 取消收藏（参数：targetType, targetId）
   - GET /api/favorite/list - 我的收藏列表（分页，按收藏时间倒序）
     - 返回收藏记录 + 对应目标的简要信息（标题、封面图）
     - 可以用 UNION 查询或在 service 层根据 targetType 分别查询再组装
   - GET /api/favorite/check - 检查是否已收藏（参数：targetType, targetId）

2. ApiUserController 中补充：
   - GET /api/user/activity/list - 我的报名列表（含活动信息）

【前端】

1. views/User/Layout.vue - 用户中心布局
   - 左侧菜单：个人信息、我的收藏、我的报名、修改密码
   - 右侧内容区 router-view
   - 顶部显示用户头像和昵称

2. views/User/Profile.vue - 个人信息
   - 头像上传（调用上传接口）
   - 昵称、手机号、邮箱、性别（el-form）
   - 保存按钮

3. views/User/Favorite.vue - 我的收藏
   - Tab 切换：资讯、景点、美食、酒店、活动、文创
   - 每个 Tab 下列出收藏的内容（卡片）
   - 取消收藏按钮
   - 空状态提示

4. views/User/Activity.vue - 我的报名
   - 报名记录列表：活动封面 + 名称 + 报名时间 + 状态 + 取消报名按钮
   - 空状态

5. views/User/Password.vue - 修改密码
   - 旧密码 + 新密码 + 确认新密码
   - 校验：新密码长度、两次输入一致
   - 提交后提示重新登录

6. 全局功能：
   - 在资讯详情、景点详情等页面添加收藏按钮（心形图标）
   - 点击收藏/取消收藏调用接口，未登录提示登录
   - 收藏状态从 /api/favorite/check 获取

7. api/user.js 和 api/favorite.js - 接口封装

要求：
- 所有用户中心页面需要登录，路由守卫检查
- 收藏列表的目标信息查询可以在后端 service 层组装（推荐）或前端分别查询
- 头像上传用 el-upload，调用 /api/admin/upload/image（用户也可以用同一个上传接口，或单独建一个 /api/user/upload/avatar）
- 取消报名需要更新活动的 signup_count -1（注意只有未开始的活动才能取消）
```

---

### 阶段七：后台管理系统

#### 提示词 7.1：实现后台通用 CRUD 页面模板

```
请帮我实现后台管理系统的通用 CRUD 功能。由于有 10+ 个管理模块，结构类似，请先做一个通用模板，然后可以快速复制。

【后端 - 通用后台 CRUD Controller】

请为以下每个模块创建 Admin Controller，包含标准的 5 个接口：
- GET /api/admin/xxx/list - 分页列表（支持关键词搜索、状态筛选）
- GET /api/admin/xxx/{id} - 详情
- POST /api/admin/xxx - 新增
- PUT /api/admin/xxx - 修改
- DELETE /api/admin/xxx/{id} - 删除

模块列表：
1. BannerAdminController（/api/admin/banner）
2. NewsAdminController（/api/admin/news）- 额外支持分类筛选
3. CategoryAdminController（/api/admin/category）
4. TicketAdminController（/api/admin/ticket）
5. VenueAdminController（/api/admin/venue）
6. ActivityAdminController（/api/admin/activity）- 额外接口：GET 报名记录列表
7. PavilionAdminController（/api/admin/pavilion）
8. HeritageAdminController（/api/admin/heritage）
9. ProductAdminController（/api/admin/product）
10. ScenicAdminController（/api/admin/scenic）
11. FoodAdminController（/api/admin/food）
12. HotelAdminController（/api/admin/hotel）
13. GuideAdminController（/api/admin/guide）
14. OrgAdminController（/api/admin/org）

每个 Controller：
- 继承或使用通用的 Service（MyBatis-Plus IService 已提供 save/update/removeById/page/getById）
- list 接口用 QueryWrapper 构建条件，支持 keyword（标题/名称 LIKE）和 status
- 分页用 Page 对象
- 返回统一 Result

【前端 - 通用列表页组件】

创建一个可复用的后台列表页 mixin 或组件模式：

1. 每个管理模块的列表页包含：
   - 顶部：搜索表单（关键词输入框 + 状态下拉 + 搜索/重置按钮）+ 新增按钮
   - 中间：el-table 数据表格（列根据模块不同）
     - 通用列：ID、封面图（缩略图）、名称/标题、排序、状态（开关切换）、创建时间、操作（编辑/删除）
   - 底部：el-pagination 分页

2. 每个模块的新增/编辑页：
   - el-dialog 弹窗或独立路由页面
   - el-form 表单（字段根据模块不同）
   - 封面图上传（el-upload + 图片上传接口）
   - 富文本编辑器（内容字段用 wangEditor）
   - 表单校验 + 提交

请先完整实现【资讯管理】模块作为示范（列表页 + 新增/编辑弹窗），然后告诉我其他模块如何快速复制。

资讯管理列表页表格列：ID、封面、标题、分类、作者、浏览量、是否置顶、状态、发布时间、操作
资讯管理表单字段：标题、分类（下拉）、封面图（上传）、摘要（textarea）、内容（富文本）、作者、是否置顶（开关）、状态（下拉：草稿/发布）

要求：
- 后端代码结构统一，可以用代码生成器先生成再补充
- 前端列表页用组合式 API，数据获取、搜索、分页、删除逻辑封装好
- 删除操作有确认对话框（el-message-box.confirm）
- 状态切换用 el-switch，调用修改接口
- 富文本编辑器封装成组件 RichEditor.vue，用 wangEditor
- 图片上传封装成组件 UploadImage.vue，支持单图上传和预览
```

#### 提示词 7.2：实现后台仪表盘和用户管理

```
请帮我实现后台仪表盘和用户管理功能。

【后端】

1. AdminDashboardController.java（/api/admin/dashboard）
   - GET /api/admin/dashboard/stats - 统计数据
     - 用户总数、资讯总数、活动总数、景点总数
     - 今日新增用户数、今日发布资讯数
     - 活动报名总次数
     - 返回 Map
   - GET /api/admin/dashboard/trend - 资讯发布趋势（最近 7 天或 30 天）
     - 返回日期数组 + 每天发布数量数组
   - GET /api/admin/dashboard/hot-news - 热门资讯 TOP10（浏览量排序）

2. AdminUserController.java（/api/admin/user）
   - GET /api/admin/user/list - 用户列表（分页，支持关键词搜索用户名/昵称）
   - GET /api/admin/user/{id} - 用户详情
   - PUT /api/admin/user/status - 启用/禁用用户
   - DELETE /api/admin/user/{id} - 删除用户
   - PUT /api/admin/user/password/reset - 重置用户密码为 123456

【前端】

1. views/Dashboard/index.vue - 仪表盘
   - 第一行：4 个统计卡片（el-card），分别显示：用户总数、资讯总数、活动总数、报名总数
     - 每个卡片有图标、数字、较昨日增长（可选）
   - 第二行：左侧 ECharts 折线图（资讯发布趋势，最近 7 天），占 2/3 宽度
     - 右侧：热门资讯 TOP5 列表，占 1/3 宽度
   - 数据从接口获取，onMounted 加载
   - ECharts 用组合式 API 封装，自适应容器宽度

2. views/User/index.vue - 用户管理
   - 搜索：用户名/昵称关键词 + 状态筛选
   - 表格列：ID、头像、用户名、昵称、手机号、性别、状态（开关）、注册时间、操作（重置密码/删除）
   - 分页
   - 重置密码有确认对话框
   - 不提供编辑用户信息功能（用户自己改）

3. views/Profile/index.vue - 个人设置
   - 管理员信息展示
   - 修改密码表单（旧密码 + 新密码 + 确认）

4. api/dashboard.js 和 api/adminUser.js - 接口封装

要求：
- 仪表盘统计卡片用渐变色背景 + 白色大数字 + 图标
- ECharts 图表要设置响应式（window resize 时调用 chart.resize）
- 趋势图数据如果某天没有数据补 0
- 用户管理的状态切换调用接口，失败时回滚开关状态
- 所有操作有成功/失败提示
```

---

### 阶段八：搜索 + 联调 + 优化

#### 提示词 8.1：实现全站搜索功能

```
请帮我实现全站搜索功能。

【后端】

1. ApiSearchController.java（/api/search）
   - GET /api/search?keyword=xxx&type=all&page=1&size=10
   - 支持 type 参数：all（全部）、news（资讯）、scenic（景点）、food（美食）、hotel（酒店）、activity（活动）、product（文创）、heritage（非遗）、pavilion（展馆）、guide（攻略）
   - all 类型时，同时搜索多张表，用 UNION ALL 合并结果
     - 统一返回字段：id, type, typeName, title, coverImage, summary, createTime
     - 按相关度或创建时间排序
   - 单类型时，搜索对应表的标题/名称字段
   - 分页返回

实现方式（二选一）：
方案 A（推荐，简单）：在 Service 层分别查询各表，然后组装成统一格式的列表，内存中分页
方案 B：用 MyBatis XML 写 UNION ALL SQL

【前端】

1. views/Search/index.vue - 搜索结果页
   - 顶部：搜索框（大尺寸，带搜索按钮，默认显示 URL 中的 keyword）
   - 搜索结果分类 Tab：全部、资讯、景点、美食、酒店、活动、文创、非遗
   - 结果列表：每条显示类型标签 + 标题（关键词高亮）+ 摘要（关键词高亮）+ 时间
   - 点击跳转到对应详情页
   - 分页
   - 空结果提示"未找到相关内容"

2. 头部搜索框改造
   - 输入关键词回车跳转到 /search?keyword=xxx
   - 支持搜索建议（可选，时间不够可省略）

3. api/search.js - 接口封装

要求：
- 关键词高亮用前端处理：将结果文本中的关键词用 <span style="color:red"> 包裹
- 搜索结果按类型分组显示或统一列表 + Tab 筛选
- 全部搜索时如果结果太多，可以每类限制数量或统一分页
- URL 参数同步（keyword, type, page），刷新保持状态
- 搜索框自动聚焦
```

#### 提示词 8.2：前后端联调与整体优化

```
请帮我进行前后端联调和整体优化检查。

【需要检查和优化的清单】

1. 接口联调检查
   - 所有前端 api/*.js 中的接口路径与后端 Controller 路径一致
   - 请求参数名一致（驼峰 vs 下划线统一）
   - 响应数据结构匹配（Result.code / Result.data）
   - 分页字段名一致（records / total / current / size）

2. 跨域与代理
   - 开发环境 Vite proxy 配置正确
   - 后端 CorsConfig 允许所有来源或指定前端地址
   - 生产环境 Nginx 反向代理配置（写在部署文档中）

3. 认证流程
   - 登录成功后 token 存入 localStorage
   - axios 请求拦截器正确携带 token（Authorization: Bearer xxx）
   - 后端拦截器从 header 解析 token，设置 userId 到 request attribute
   - 401 时前端清除 token 并跳转登录页
   - 需要登录的操作（收藏、报名）未登录时提示并跳转登录

4. 图片上传与访问
   - 上传接口返回的 URL 路径正确（/uploads/xxx.jpg）
   - 后端静态资源映射正确（WebMvcConfig addResourceHandlers）
   - 前端图片 URL 拼接正确（如果后端返回完整路径就直接用）
   - 图片加载失败有默认占位图

5. 富文本处理
   - 后台富文本编辑器上传图片正常
   - 前台详情页富文本渲染样式正常（图片不溢出、段落有间距）
   - 富文本内容中的图片 URL 是可访问的相对路径或完整路径

6. 页面优化
   - 所有列表页有加载状态（loading）和空状态
   - 所有操作有成功/失败提示（ElMessage）
   - 删除操作有确认对话框
   - 表单有校验和提交按钮 loading 状态
   - 路由切换时页面滚动到顶部
   - 页面标题随路由变化（document.title）

7. 移动端适配（基础）
   - 导航栏在小屏幕显示汉堡菜单
   - 列表网格在小屏幕减少列数
   - 字体大小适配
   - 不要求完美适配，但不能严重错位

8. 安全检查
   - 密码传输不打印日志
   - SQL 注入：所有查询用参数化，无字符串拼接
   - XSS：富文本内容可考虑简单过滤（引入 hutool 的 HtmlFilter 或 jsoup）
   - 越权：用户只能操作自己的数据（收藏、报名时从 token 取 userId，不信前端传的 userId）
   - 后台接口都需要管理员 token

请逐项检查，列出发现的问题和修复建议。然后生成一个联调测试用例清单（按模块列出需要测试的功能点）。
```

---

### 附录：通用辅助提示词

#### 提示词 A：生成某个模块的完整前后端代码

```
请帮我完整实现【XX模块】的前后端代码。

模块说明：[描述这个模块的功能]
数据表：[表名和字段]
前端页面：[列表页 + 详情页/表单页]

请输出：
1. 后端 Entity 类
2. 后端 Mapper 接口
3. 后端 Service 接口和实现
4. 后端前台 Controller（list + detail）
5. 后端后台 Controller（CRUD）
6. 前端 api 封装
7. 前端列表页
8. 前端详情页（如果有）
9. 后台管理列表页
10. 后台管理表单组件

要求：
- 代码完整可运行，不要省略
- 中文注释
- 遵循项目已有的代码规范（统一 Result、分页、JWT 认证等）
- 前端用组合式 API + Element Plus
```

#### 提示词 B：修复 Bug

```
请帮我修复以下问题：

问题描述：[详细描述 Bug 现象]
复现步骤：[如何复现]
期望结果：[应该是什么样]
实际结果：[实际是什么样]
浏览器控制台报错：[如果有报错信息贴在这里]

相关代码文件：
[列出相关的文件路径和关键代码片段]

请分析问题原因并给出修复方案，输出修改后的完整代码。
```

#### 提示词 C：代码审查

```
请帮我审查以下代码，检查是否存在：
1. 语法错误或逻辑错误
2. 安全漏洞（SQL 注入、XSS、越权、敏感信息泄露）
3. 性能问题（N+1 查询、大循环、未分页）
4. 代码规范问题（命名、注释、重复代码）
5. 边界条件处理（空值、异常、并发）

代码如下：
[贴代码]

请按问题严重程度排序，给出具体的修改建议和修改后的代码。
```

---

## 第三部分：Vibe Coding 工作流说明

### 什么是 Vibe Coding

Vibe Coding 是一种以 AI 编程工具为核心的开发方式：你用自然语言描述需求（"vibe"），AI 生成代码，你负责审查、调整和整合。核心是"描述 → 生成 → 验证 → 迭代"的循环。

### 本项目推荐的 Vibe Coding 工作流

#### 工具选择
- **AI 编程工具**：豆包（对话式，适合生成大块代码和方案）、Cursor / Windsurf（IDE 集成，适合边写边改）、Claude（代码理解能力强）
- **建议组合**：用豆包做整体规划和大块代码生成 → 复制到 IDEA/VS Code → 用 Cursor 做局部修改和调试

#### 工作流步骤

```
┌─────────────────────────────────────────────────────┐
│  第零步：准备阶段（一次性）                              │
│  ✅ 安装 JDK 1.8、Maven、MySQL 8、Node.js 18+       │
│  ✅ 安装 IDEA、VS Code、Navicat/DBeaver               │
│  ✅ 阅读本实施方案，理解整体架构                         │
│  ✅ 创建 Gitee 私有仓库，初始化 Git                     │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│  第一步：按阶段执行，每个阶段一个循环                     │
│                                                       │
│  对每个开发阶段：                                       │
│  ┌─────────────────────────────────────────────┐    │
│  │ 1. 阅读对应阶段的提示词                         │    │
│  │ 2. 复制提示词到 AI 工具，生成代码               │    │
│  │ 3. 将生成的代码放入项目对应位置                  │    │
│  │ 4. 编译/运行，检查报错                          │    │
│  │ 5. 把报错信息贴回 AI，让它修复                   │    │
│  │ 6. 手动验证功能是否正常                          │    │
│  │ 7. Git 提交一次（阶段完成或功能完成时）           │    │
│  └─────────────────────────────────────────────┘    │
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│  第二步：核心原则                                        │
│                                                       │
│  📌 一次只让 AI 做一件事：不要一次生成整个项目，按模块/文件来
│  📌 生成后必须读一遍：AI 生成的代码可能有幻觉（不存在的方法/字段）
│  📌 报错直接贴给 AI：把完整报错信息复制过去，通常能直接修复
│  📌 小步快跑：每完成一个小功能就运行验证，不要攒一大堆再测
│  📌 保留人工判断：架构设计、数据库设计这些关键决策要自己想清楚
│  📌 及时提交 Git：每完成一个可运行的版本就 commit，方便回滚
└──────────────────────┬──────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│  第三步：常见问题处理                                    │
│                                                       │
│  ❓ AI 生成的代码有编译错误？                           │
│     → 把错误信息完整贴给它，说"修复以上错误"，通常 1-2 轮解决
│                                                       │
│  ❓ AI 生成的方法/类不存在？                            │
│     → 明确告诉它项目里已有的类和方法，让它基于现有代码修改
│     → 或者让它先输出需要新增哪些文件，再逐个生成
│                                                       │
│  ❓ 前后端联调数据对不上？                              │
│     → 用 Apifox/Postman 先测后端接口，确认数据格式
│     → 再检查前端 axios 封装和字段映射
│                                                       │
│  ❓ 功能太复杂 AI 一次生成不好？                         │
│     → 拆分成更小的任务：先生成列表页，再生成详情页，再做表单
│     → 用"提示词 A：生成某个模块的完整前后端代码"但限定范围
│                                                       │
│  ❓ 不知道下一步做什么？                                 │
│     → 回到本方案的"开发计划与里程碑"，按顺序推进
│     → 先做 P0 模块（首页、资讯、旅游、用户、后台），再做 P1
└─────────────────────────────────────────────────────┘
```

#### 每日开发节奏建议

```
上午（2小时）：
  1. 回顾昨天进度，确定今天目标（1-2 个小功能）
  2. 用 AI 生成代码，放入项目
  3. 编译运行，修复报错

下午（2小时）：
  4. 功能测试，手动验证
  5. 样式调整（AI 生成的样式通常需要微调）
  6. Git 提交

晚上（可选1小时）：
  7. 写开发日志/笔记
  8. 规划明天任务
```

#### 项目完成检查清单

- [ ] 后端项目能正常启动，无报错
- [ ] 前端前台项目能正常运行，所有页面可访问
- [ ] 前端后台项目能正常运行，所有 CRUD 功能正常
- [ ] MySQL 数据库表创建完成，有测试数据
- [ ] 用户注册登录功能正常
- [ ] 资讯列表/详情正常
- [ ] 旅游模块（景点/美食/酒店）列表/详情正常
- [ ] 活动报名功能正常
- [ ] 收藏功能正常
- [ ] 后台登录正常
- [ ] 后台各模块增删改查正常
- [ ] 图片上传正常
- [ ] 全站搜索正常
- [ ] 前后端联调无明显 Bug
- [ ] 毕设论文完成
- [ ] 演示 PPT 完成
- [ ] 项目打包（后端 Jar + 前端 dist）

---

> **最后提醒**：毕设的核心是"能跑通 + 能讲清楚"，不要追求完美。遇到卡住的功能，先跳过做标记，保证整体流程完整。答辩时重点展示你做了什么、用了什么技术，没做的部分说"未来可扩展"即可。祝顺利通过！
