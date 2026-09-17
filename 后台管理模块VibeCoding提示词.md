# 寿阳文旅云 - 后台管理模块 Vibe Coding 提示词

> 本文件按照优先级排序，用于快速生成后台管理系统剩余 12 个模块的前端页面。
> 后端接口已全部就绪，只需按照「资讯管理」模板复制修改即可。
> 生成时间：2026-09-17

---

## 目录

- [使用说明](#使用说明)
- [通用模板提示词（必读）](#通用模板提示词必读)
- [高优先级模块（答辩演示必需）](#高优先级模块答辩演示必需)
  - [1. 轮播图管理](#1-轮播图管理)
  - [2. 分类管理](#2-分类管理)
  - [3. 活动管理](#3-活动管理)
  - [4. 景点管理](#4-景点管理)
  - [5. 美食管理](#5-美食管理)
- [中优先级模块（提升完成度）](#中优先级模块提升完成度)
  - [6. 场馆管理](#6-场馆管理)
  - [7. 票务管理](#7-票务管理)
  - [8. 酒店管理](#8-酒店管理)
  - [9. 文创管理](#9-文创管理)
  - [10. 非遗管理](#10-非遗管理)
- [低优先级模块（可选）](#低优先级模块可选)
  - [11. 展馆管理](#11-展馆管理)
  - [12. 攻略管理](#12-攻略管理)
  - [13. 单位管理](#13-单位管理)
- [路由配置更新](#路由配置更新)

---

## 使用说明

### 前置条件
- 后端服务已启动（端口 8080），所有 CRUD 接口已就绪
- 后台前端工程已启动（端口 5174）
- 「资讯管理」模块已完整实现，作为复制模板
- 通用组件已创建：`UploadImage.vue`（图片上传）、`RichEditor.vue`（富文本编辑器）

### 复制步骤（每个模块）
1. 复制 `src/views/News/index.vue` 为对应模块的 `src/views/XXX/index.vue`
2. 修改 API 路径（3 处：list / save / update / delete）
3. 修改表格列（el-table-column）
4. 修改表单字段（el-form-item）
5. 修改路由配置（`src/router/index.js` 中对应模块的 component 路径）
6. 测试列表、新增、编辑、删除、状态切换功能

### 通用 API 路径规则
- 列表：`GET /api/admin/xxx/list?page=1&size=10&keyword=xxx&status=1`
- 详情：`GET /api/admin/xxx/{id}`
- 新增：`POST /api/admin/xxx`
- 修改：`PUT /api/admin/xxx`
- 删除：`DELETE /api/admin/xxx/{id}`

---

## 通用模板提示词（必读）

> 将以下提示词复制到 AI 对话框，替换其中的 `【模块名】`、`【表名】`、`【字段列表】` 等占位符，即可生成对应模块的完整页面代码。

```
请帮我实现后台管理系统的【模块名】模块页面，文件路径为 src/views/【模块名】/index.vue。

【技术栈】
- Vue3 + 组合式 API + setup 语法糖
- Element Plus 组件库
- Pinia 状态管理
- Axios 请求封装（@/utils/request，已封装 get/post/put/del 方法，baseURL=/api）

【参考模板】
参考已实现的资讯管理页面 src/views/News/index.vue，保持相同的代码结构和风格：
- 顶部搜索表单（关键词输入框 + 状态下拉 + 搜索/重置按钮）+ 新增按钮
- 中间 el-table 数据表格
- 底部 el-pagination 分页
- 新增/编辑用 el-dialog 弹窗 + el-form 表单
- 删除用 el-message-box.confirm 确认对话框
- 状态切换用 el-switch，调用修改接口，失败回滚

【API 接口】
- 列表：GET /admin/【表名】/list，参数：page, size, keyword, status（返回 Page 对象，含 records/total）
- 新增：POST /admin/【表名】
- 修改：PUT /admin/【表名】
- 删除：DELETE /admin/【表名】/{id}

【表格列】
【根据模块填写，例如：ID、封面图、名称、排序、状态、创建时间、操作】

【表单字段】
【根据模块填写，例如：名称、封面图（UploadImage组件）、排序、状态（radio）】

【要求】
1. 代码注释用中文
2. 所有操作有 ElMessage 成功/失败提示
3. 表单有校验（必填项）
4. 提交按钮有 loading 状态
5. 图片上传用已封装的 UploadImage 组件（v-model 绑定图片URL）
6. 如有富文本内容，用已封装的 RichEditor 组件（v-model 绑定HTML）
7. 状态字段：0=禁用/下架，1=启用/发布
8. 分页支持每页条数切换（10/20/50）
9. 搜索和重置后页码重置为1
10. 删除后刷新列表
```

---

## 高优先级模块（答辩演示必需）

### 1. 轮播图管理

**文件路径：** `src/views/Banner/index.vue`
**API 前缀：** `/admin/banner`

**表格列：** ID、封面图（缩略图）、标题、跳转链接、排序、状态、创建时间、操作

**表单字段：**
- 标题（input，必填）
- 封面图（UploadImage，必填）
- 跳转链接（input，可选，如 /news、/travel/scenic）
- 排序（input number，默认 0，数字越小越靠前）
- 状态（radio：0禁用 / 1启用，默认 1）

**Vibe 提示词：**
```
请帮我实现后台管理系统的轮播图管理页面，文件路径为 src/views/Banner/index.vue。

参考资讯管理页面 src/views/News/index.vue 的结构和风格。

API 接口：
- 列表：GET /admin/banner/list?page=1&size=10&keyword=xxx&status=1
- 新增：POST /admin/banner
- 修改：PUT /admin/banner
- 删除：DELETE /admin/banner/{id}

表格列：ID、封面图（60x40缩略图）、标题、跳转链接、排序、状态（el-switch）、创建时间、操作（编辑/删除）

表单字段（el-dialog弹窗）：
- 标题：el-input，必填，maxlength=50
- 封面图：UploadImage组件，v-model绑定coverImage，必填
- 跳转链接：el-input，可选，placeholder="如 /news 或 https://..."
- 排序：el-input-number，默认0，范围0-999，数字越小越靠前
- 状态：el-radio-group，0=禁用，1=启用，默认1

要求：
1. 组合式API + setup语法糖，中文注释
2. 搜索表单：关键词（标题LIKE）+ 状态下拉 + 搜索/重置按钮
3. 所有操作有ElMessage提示，删除有确认对话框
4. 状态切换调用修改接口，失败回滚
5. 分页支持10/20/50条切换
```

---

### 2. 分类管理

**文件路径：** `src/views/Category/index.vue`
**API 前缀：** `/admin/category`
**额外接口：** `GET /admin/category/all`（获取所有分类，下拉选用）

**表格列：** ID、分类名称、排序、状态、创建时间、操作

**表单字段：**
- 分类名称（input，必填）
- 排序（input number，默认 0）
- 状态（radio：0禁用 / 1启用，默认 1）

**Vibe 提示词：**
```
请帮我实现后台管理系统的分类管理页面，文件路径为 src/views/Category/index.vue。

参考资讯管理页面 src/views/News/index.vue 的结构和风格。

API 接口：
- 列表：GET /admin/category/list?page=1&size=10&keyword=xxx&status=1
- 新增：POST /admin/category
- 修改：PUT /admin/category
- 删除：DELETE /admin/category/{id}

表格列：ID、分类名称、排序、状态（el-switch）、创建时间、操作（编辑/删除）

表单字段（el-dialog弹窗）：
- 分类名称：el-input，必填，maxlength=20
- 排序：el-input-number，默认0，范围0-999
- 状态：el-radio-group，0=禁用，1=启用，默认1

要求：
1. 组合式API + setup语法糖，中文注释
2. 搜索表单：关键词（分类名称LIKE）+ 状态下拉 + 搜索/重置按钮
3. 所有操作有ElMessage提示，删除有确认对话框
4. 状态切换调用修改接口，失败回滚
5. 分页支持10/20/50条切换
6. 分类管理比较简单，不需要封面图和富文本
```

---

### 3. 活动管理

**文件路径：** `src/views/Activity/index.vue`
**API 前缀：** `/admin/activity`
**额外接口：** `GET /admin/activity/register/list`（报名记录列表）

**表格列：** ID、封面图、活动标题、活动地点、开始时间、报名人数/上限、状态、创建时间、操作

**表单字段：**
- 活动标题（input，必填）
- 封面图（UploadImage，必填）
- 活动地点（input，必填）
- 开始时间（datetime picker，必填）
- 结束时间（datetime picker，必填）
- 报名截止时间（datetime picker，必填）
- 人数上限（input number，0表示不限）
- 活动详情（RichEditor 富文本）
- 排序（input number，默认 0）
- 状态（radio：0未开始 / 1进行中 / 2已结束，默认 0）

**额外功能：** 报名记录查看按钮，点击弹出对话框显示该活动的报名记录列表

**Vibe 提示词：**
```
请帮我实现后台管理系统的活动管理页面，文件路径为 src/views/Activity/index.vue。

参考资讯管理页面 src/views/News/index.vue 的结构和风格，活动管理稍复杂。

API 接口：
- 列表：GET /admin/activity/list?page=1&size=10&keyword=xxx&status=1
- 新增：POST /admin/activity
- 修改：PUT /admin/activity
- 删除：DELETE /admin/activity/{id}
- 报名记录：GET /admin/activity/register/list?activityId=xxx&page=1&size=10

表格列：ID、封面图（60x40）、活动标题、活动地点、开始时间、已报名/上限（如 12/50）、状态标签、创建时间、操作（报名记录/编辑/删除）

状态标签颜色：0=未开始（info灰）、1=进行中（success绿）、2=已结束（warning橙）

表单字段（el-dialog弹窗，宽度800px）：
- 活动标题：el-input，必填，maxlength=100
- 封面图：UploadImage组件，必填
- 活动地点：el-input，必填
- 开始时间：el-date-picker type="datetime"，必填
- 结束时间：el-date-picker type="datetime"，必填
- 报名截止时间：el-date-picker type="datetime"，必填
- 人数上限：el-input-number，默认0，0表示不限
- 活动详情：RichEditor富文本编辑器，高度300px
- 排序：el-input-number，默认0
- 状态：el-radio-group，0=未开始，1=进行中，2=已结束，默认0

额外功能：
- 操作列增加"报名记录"按钮，点击弹出el-dialog，显示该活动的报名记录表格（姓名、手机号、备注、报名时间），支持分页
- 报名记录对话框宽度700px

要求：
1. 组合式API + setup语法糖，中文注释
2. 搜索表单：关键词（标题LIKE）+ 状态下拉 + 搜索/重置按钮
3. 所有操作有ElMessage提示，删除有确认对话框
4. 状态切换调用修改接口，失败回滚
5. 分页支持10/20/50条切换
6. 时间格式化显示为 YYYY-MM-DD HH:mm
```

---

### 4. 景点管理

**文件路径：** `src/views/Scenic/index.vue`
**API 前缀：** `/admin/scenic`

**表格列：** ID、封面图、景点名称、等级、地址、门票价格、状态、创建时间、操作

**表单字段：**
- 景点名称（input，必填）
- 封面图（UploadImage，必填）
- 等级（select：5A/4A/3A/2A/1A/其他）
- 地址（input，必填）
- 开放时间（input，如 "08:00-18:00"）
- 门票价格（input，如 "免费" 或 "80元"）
- 联系电话（input）
- 景点描述（textarea）
- 景点详情（RichEditor 富文本）
- 浏览量（input number，只读或可手动设置）
- 排序（input number，默认 0）
- 状态（radio：0下架 / 1上架，默认 1）

**Vibe 提示词：**
```
请帮我实现后台管理系统的景点管理页面，文件路径为 src/views/Scenic/index.vue。

参考资讯管理页面 src/views/News/index.vue 的结构和风格。

API 接口：
- 列表：GET /admin/scenic/list?page=1&size=10&keyword=xxx&status=1
- 新增：POST /admin/scenic
- 修改：PUT /admin/scenic
- 删除：DELETE /admin/scenic/{id}

表格列：ID、封面图（60x40）、景点名称、等级（el-tag）、地址、门票价格、状态（el-switch）、创建时间、操作（编辑/删除）

表单字段（el-dialog弹窗，宽度800px）：
- 景点名称：el-input，必填，maxlength=50
- 封面图：UploadImage组件，必填
- 等级：el-select，选项：5A、4A、3A、2A、1A、其他
- 地址：el-input，必填
- 开放时间：el-input，placeholder="如 08:00-18:00"
- 门票价格：el-input，placeholder="如 免费 或 80元"
- 联系电话：el-input
- 景点描述：el-input type="textarea" :rows="3"
- 景点详情：RichEditor富文本编辑器，高度300px
- 浏览量：el-input-number，默认0
- 排序：el-input-number，默认0
- 状态：el-radio-group，0=下架，1=上架，默认1

要求：
1. 组合式API + setup语法糖，中文注释
2. 搜索表单：关键词（名称LIKE）+ 状态下拉 + 搜索/重置按钮
3. 所有操作有ElMessage提示，删除有确认对话框
4. 状态切换调用修改接口，失败回滚
5. 分页支持10/20/50条切换
6. 等级标签用不同颜色：5A=danger红，4A=warning橙，3A=primary蓝，其他=info灰
```

---

### 5. 美食管理

**文件路径：** `src/views/Food/index.vue`
**API 前缀：** `/admin/food`

**表格列：** ID、封面图、美食名称、分类、地址、状态、创建时间、操作

**表单字段：**
- 美食名称（input，必填）
- 封面图（UploadImage，必填）
- 分类（select：主食/小吃/菜肴/甜品/其他）
- 地址（input）
- 价格（input，如 "15元"）
- 美食描述（textarea）
- 美食详情（RichEditor 富文本）
- 排序（input number，默认 0）
- 状态（radio：0下架 / 1上架，默认 1）

**Vibe 提示词：**
```
请帮我实现后台管理系统的美食管理页面，文件路径为 src/views/Food/index.vue。

参考资讯管理页面 src/views/News/index.vue 的结构和风格。

API 接口：
- 列表：GET /admin/food/list?page=1&size=10&keyword=xxx&status=1
- 新增：POST /admin/food
- 修改：PUT /admin/food
- 删除：DELETE /admin/food/{id}

表格列：ID、封面图（60x40）、美食名称、分类（el-tag）、地址、状态（el-switch）、创建时间、操作（编辑/删除）

表单字段（el-dialog弹窗，宽度800px）：
- 美食名称：el-input，必填，maxlength=50
- 封面图：UploadImage组件，必填
- 分类：el-select，选项：主食、小吃、菜肴、甜品、其他
- 地址：el-input
- 价格：el-input，placeholder="如 15元"
- 美食描述：el-input type="textarea" :rows="3"
- 美食详情：RichEditor富文本编辑器，高度300px
- 排序：el-input-number，默认0
- 状态：el-radio-group，0=下架，1=上架，默认1

要求：
1. 组合式API + setup语法糖，中文注释
2. 搜索表单：关键词（名称LIKE）+ 状态下拉 + 搜索/重置按钮
3. 所有操作有ElMessage提示，删除有确认对话框
4. 状态切换调用修改接口，失败回滚
5. 分页支持10/20/50条切换
```

---

## 中优先级模块（提升完成度）

### 6. 场馆管理

**文件路径：** `src/views/Venue/index.vue`
**API 前缀：** `/admin/venue`

**表格列：** ID、封面图、场馆名称、地址、开放时间、状态、创建时间、操作

**表单字段：** 名称、封面图、地址、开放时间、联系电话、场馆描述、场馆详情（RichEditor）、排序、状态

**提示词：** 参考「景点管理」，去掉等级/门票价格字段，增加"容纳人数"字段。

---

### 7. 票务管理

**文件路径：** `src/views/Ticket/index.vue`
**API 前缀：** `/admin/ticket`

**表格列：** ID、封面图、票务名称、价格、状态、创建时间、操作

**表单字段：** 名称、封面图、价格（decimal）、原价、库存、使用说明、购票须知（RichEditor）、排序、状态

**提示词：** 参考「美食管理」，增加价格/原价/库存字段。

---

### 8. 酒店管理

**文件路径：** `src/views/Hotel/index.vue`
**API 前缀：** `/admin/hotel`

**表格列：** ID、封面图、酒店名称、星级、地址、价格区间、状态、创建时间、操作

**表单字段：** 名称、封面图、星级（select：2-5星）、地址、联系电话、价格区间（input，如 "200-500元"）、酒店描述、酒店详情（RichEditor）、排序、状态

**提示词：** 参考「景点管理」，等级改为星级，门票价格改为价格区间。

---

### 9. 文创管理

**文件路径：** `src/views/Product/index.vue`
**API 前缀：** `/admin/product`

**表格列：** ID、封面图、商品名称、价格、库存、状态、创建时间、操作

**表单字段：** 名称、封面图、价格（decimal）、原价、库存、商品描述、商品详情（RichEditor）、多图（可选，JSON数组）、排序、状态

**提示词：** 参考「票务管理」，字段基本一致。

---

### 10. 非遗管理

**文件路径：** `src/views/Heritage/index.vue`
**API 前缀：** `/admin/heritage`

**表格列：** ID、封面图、非遗名称、级别、类别、传承人、状态、创建时间、操作

**表单字段：** 名称、封面图、级别（select：国家级/省级/市级/县级）、类别（input）、传承人（input）、非遗描述、非遗详情（RichEditor）、排序、状态

**提示词：** 参考「景点管理」，等级改为非遗级别，增加类别/传承人字段。

---

## 低优先级模块（可选）

### 11. 展馆管理

**文件路径：** `src/views/Pavilion/index.vue`
**API 前缀：** `/admin/pavilion`

**表格列：** ID、封面图、展馆名称、浏览量、状态、创建时间、操作

**表单字段：** 名称、封面图、展馆描述、展馆详情（RichEditor）、多图（可选）、浏览量、排序、状态

---

### 12. 攻略管理

**文件路径：** `src/views/Guide/index.vue`
**API 前缀：** `/admin/guide`

**表格列：** ID、封面图、攻略标题、作者、天数、浏览量、状态、创建时间、操作

**表单字段：** 标题、封面图、作者、天数（input number）、攻略描述、攻略详情（RichEditor）、浏览量、排序、状态

---

### 13. 单位管理

**文件路径：** `src/views/Org/index.vue`
**API 前缀：** `/admin/org`

**表格列：** ID、封面图、单位名称、单位类型、联系电话、状态、创建时间、操作

**表单字段：** 名称、封面图、单位类型（select：政府部门/事业单位/企业/其他）、地址、联系电话、单位描述、排序、状态

---

## 路由配置更新

每完成一个模块，需要在 `src/router/index.js` 中更新对应路由的 component 路径：

```javascript
// 例如完成轮播图管理后，将：
{
  path: 'banner',
  name: 'Banner',
  component: () => import('@/views/Placeholder.vue'),  // 占位页
  meta: { title: '轮播图管理', icon: 'Picture' }
}

// 改为：
{
  path: 'banner',
  name: 'Banner',
  component: () => import('@/views/Banner/index.vue'),  // 实际页面
  meta: { title: '轮播图管理', icon: 'Picture' }
}
```

**需要更新的路由清单（按优先级）：**
1. `banner` → `@/views/Banner/index.vue`
2. `category` → `@/views/Category/index.vue`
3. `activity` → `@/views/Activity/index.vue`
4. `scenic` → `@/views/Scenic/index.vue`
5. `food` → `@/views/Food/index.vue`
6. `venue` → `@/views/Venue/index.vue`
7. `ticket` → `@/views/Ticket/index.vue`
8. `hotel` → `@/views/Hotel/index.vue`
9. `product` → `@/views/Product/index.vue`
10. `heritage` → `@/views/Heritage/index.vue`
11. `pavilion` → `@/views/Pavilion/index.vue`
12. `guide` → `@/views/Guide/index.vue`
13. `org` → `@/views/Org/index.vue`

---

## 完成验证清单

每完成一个模块，按以下清单验证：

- [ ] 列表页正常显示数据，分页正常
- [ ] 关键词搜索正常
- [ ] 状态筛选正常
- [ ] 新增功能正常（表单校验、提交成功、列表刷新）
- [ ] 编辑功能正常（回显数据、修改保存）
- [ ] 删除功能正常（确认对话框、删除成功、列表刷新）
- [ ] 状态切换正常（开关切换、调用接口、失败回滚）
- [ ] 图片上传正常（上传、预览、更换、删除）
- [ ] 富文本编辑器正常（输入、插入图片、保存）
- [ ] 路由配置已更新，菜单点击进入实际页面
- [ ] 页面无控制台报错

---

> **提示：** 高优先级 5 个模块约需 1-2 小时完成，全部 13 个模块约需 3-4 小时。毕设答辩建议至少完成高优先级 5 个模块，其他模块可在演示时说明"与资讯管理结构类似，已完成后端接口"。
