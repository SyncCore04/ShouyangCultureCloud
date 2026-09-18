# 寿阳文旅云 - 后台管理模块 Vibe Coding 提示词（优化版）

> 本文件包含 13 个后台管理模块的完整 Vibe Coding 提示词，按重要程度从高到低排序。
> 每个模块的提示词都可直接复制使用，无需参考其他模块。
> 生成时间：2026-09-18

---

## 目录

- [通用说明](#通用说明)
- [第 1 优先级：轮播图管理](#第-1-优先级轮播图管理)
- [第 2 优先级：分类管理](#第-2-优先级分类管理)
- [第 3 优先级：活动管理](#第-3-优先级活动管理)
- [第 4 优先级：景点管理](#第-4-优先级景点管理)
- [第 5 优先级：美食管理](#第-5-优先级美食管理)
- [第 6 优先级：酒店管理](#第-6-优先级酒店管理)
- [第 7 优先级：场馆管理](#第-7-优先级场馆管理)
- [第 8 优先级：票务管理](#第-8-优先级票务管理)
- [第 9 优先级：非遗管理](#第-9-优先级非遗管理)
- [第 10 优先级：文创管理](#第-10-优先级文创管理)
- [第 11 优先级：展馆管理](#第-11-优先级展馆管理)
- [第 12 优先级：攻略管理](#第-12-优先级攻略管理)
- [第 13 优先级：单位管理](#第-13-优先级单位管理)
- [路由配置更新](#路由配置更新)
- [完成验证清单](#完成验证清单)

---

## 通用说明

### 前置条件
- 后端服务已启动（端口 8080），所有 CRUD 接口已就绪
- 后台前端工程已启动（端口 5174）
- 「资讯管理」模块已完整实现，作为代码风格参考
- 通用组件已创建：`UploadImage.vue`（图片上传，v-model 绑定图片URL）、`RichEditor.vue`（富文本编辑器，v-model 绑定HTML）

### 通用技术要求（所有模块共用）
```
技术栈：Vue3 + 组合式 API + setup 语法糖 + Element Plus + Pinia + Axios
代码风格：参考 src/views/News/index.vue，保持相同结构
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api
```

### 通用页面结构（所有模块共用）
```
1. 顶部搜索栏：关键词输入框 + 状态下拉 + 搜索按钮 + 重置按钮 + 新增按钮
2. 中间数据表格：el-table，操作列在最右侧
3. 底部分页：el-pagination，支持 10/20/50 条切换，显示总数
4. 新增/编辑弹窗：el-dialog，宽度根据字段数量调整
5. 删除确认：el-message-box.confirm
6. 状态开关：el-switch，切换调用修改接口，失败回滚
```

### 通用交互要求（所有模块共用）
```
1. 代码注释用中文
2. 所有操作成功/失败都有 ElMessage 提示
3. 表单必填项有校验，提交按钮有 loading 状态
4. 搜索和重置后页码重置为 1
5. 新增/编辑成功后关闭弹窗并刷新列表
6. 删除成功后刷新列表
7. 状态切换失败时开关自动回滚
```

---

## 第 1 优先级：轮播图管理

> 重要性：首页直接展示，答辩演示必看，视觉效果好

**文件路径：** `src/views/Banner/index.vue`

```
请帮我实现后台管理系统的轮播图管理页面，文件路径为 src/views/Banner/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/banner/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 详情：request.get(`/admin/banner/${id}`)
- 新增：request.post('/admin/banner', formData)
- 修改：request.put('/admin/banner', formData)
- 删除：request.delete(`/admin/banner/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入标题"）+ 状态下拉（全部/启用/禁用）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，使用 el-image，fit="cover"
   - 标题列：显示轮播图标题
   - 跳转链接列：显示跳转链接文本
   - 排序列：宽度 80，显示排序数字
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化显示 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50
   - 显示总数，支持跳转页码

【新增/编辑弹窗】
el-dialog 宽度 600px，标题根据操作类型显示「新增轮播图」或「编辑轮播图」
el-form 标签宽度 80px，字段如下：
1. 标题：el-input，必填，maxlength=50，placeholder="请输入标题"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填，提示"建议尺寸 1920x600"
3. 跳转链接：el-input，可选，placeholder="如 /news 或 https://..."
4. 排序：el-input-number，默认 0，范围 0-999，提示"数字越小越靠前"
5. 状态：el-radio-group，选项：0=禁用，1=启用，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该轮播图吗？"，确认后调用删除接口
3. 编辑：点击编辑时调用详情接口获取数据，回填到表单
4. 表单校验：标题必填，封面图必填
5. 提交 loading：提交按钮在请求期间显示 loading，防止重复提交
```

---

## 第 2 优先级：分类管理

> 重要性：资讯管理的依赖，演示必看，页面简单快速完成

**文件路径：** `src/views/Category/index.vue`

```
请帮我实现后台管理系统的分类管理页面，文件路径为 src/views/Category/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/category/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/category', formData)
- 修改：request.put('/admin/category', formData)
- 删除：request.delete(`/admin/category/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入分类名称"）+ 状态下拉（全部/启用/禁用）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 分类名称列：显示分类名称
   - 排序列：宽度 80，显示排序数字
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化显示 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 500px，标题根据操作类型显示「新增分类」或「编辑分类」
el-form 标签宽度 80px，字段如下：
1. 分类名称：el-input，必填，maxlength=20，placeholder="请输入分类名称"
2. 排序：el-input-number，默认 0，范围 0-999，提示"数字越小越靠前"
3. 状态：el-radio-group，选项：0=禁用，1=启用，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该分类吗？"，确认后调用删除接口
3. 表单校验：分类名称必填
4. 提交 loading：提交按钮在请求期间显示 loading
5. 分类管理不需要封面图和富文本编辑器，保持简洁
```

---

## 第 3 优先级：活动管理

> 重要性：核心业务功能，有报名记录查看，是演示亮点

**文件路径：** `src/views/Activity/index.vue`

```
请帮我实现后台管理系统的活动管理页面，文件路径为 src/views/Activity/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，活动管理稍复杂，多了报名记录功能。

【API 接口】
- 列表：request.get('/admin/activity/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/activity', formData)
- 修改：request.put('/admin/activity', formData)
- 删除：request.delete(`/admin/activity/${id}`)
- 报名记录：request.get('/admin/activity/register/list', { params: { activityId, page, size } })

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入活动标题"）+ 状态下拉（全部/未开始/进行中/已结束）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 活动标题列：显示活动标题
   - 活动地点列：显示地点
   - 开始时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 报名人数列：宽度 100，显示"已报名/上限"，如 12/50，人数满了红色高亮
   - 状态列：宽度 90，使用 el-tag 标签显示：
     * 0=未开始：info 灰色
     * 1=进行中：success 绿色
     * 2=已结束：warning 橙色
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 180，固定右侧，包含「报名记录」「编辑」「删除」三个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增活动」或「编辑活动」
el-form 标签宽度 90px，字段如下：
1. 活动标题：el-input，必填，maxlength=100，placeholder="请输入活动标题"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 活动地点：el-input，必填，placeholder="请输入活动地点"
4. 开始时间：el-date-picker type="datetime"，必填，格式化 YYYY-MM-DD HH:mm:ss
5. 结束时间：el-date-picker type="datetime"，必填
6. 报名截止时间：el-date-picker type="datetime"，必填
7. 人数上限：el-input-number，默认 0，范围 0-9999，提示"0 表示不限人数"
8. 活动详情：RichEditor 富文本编辑器，高度 300px
9. 排序：el-input-number，默认 0，范围 0-999
10. 状态：el-radio-group，选项：0=未开始，1=进行中，2=已结束，默认 0

【报名记录弹窗】
点击操作列的「报名记录」按钮，弹出第二个 el-dialog，宽度 700px
标题：报名记录 - {活动标题}
内容：el-table 显示报名记录，列如下：
- 序号列：宽度 60
- 姓名列：显示报名人姓名
- 手机号列：显示联系电话
- 备注列：显示备注信息
- 报名时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
底部分页：el-pagination，小型尺寸，支持 10/20 条切换

【交互要求】
1. 删除：点击删除弹出确认对话框"确认删除该活动吗？"，确认后调用删除接口
2. 编辑：点击编辑时获取详情数据，回填表单
3. 表单校验：标题、封面图、地点、开始时间、结束时间、报名截止时间必填
4. 时间校验：开始时间必须早于结束时间，报名截止时间不能晚于结束时间
5. 报名记录打开时根据 activityId 请求数据
6. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 4 优先级：景点管理

> 重要性：畅游寿阳核心内容，前台首页有展示，演示常看

**文件路径：** `src/views/Scenic/index.vue`

```
请帮我实现后台管理系统的景点管理页面，文件路径为 src/views/Scenic/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/scenic/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/scenic', formData)
- 修改：request.put('/admin/scenic', formData)
- 删除：request.delete(`/admin/scenic/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入景点名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 景点名称列：显示景点名称
   - 等级列：宽度 80，使用 el-tag 标签显示，颜色规则：
     * 5A：danger 红色
     * 4A：warning 橙色
     * 3A：primary 蓝色
     * 其他：info 灰色
   - 地址列：显示地址，超长省略
   - 门票价格列：宽度 100，显示价格
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增景点」或「编辑景点」
el-form 标签宽度 90px，字段如下：
1. 景点名称：el-input，必填，maxlength=50，placeholder="请输入景点名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 等级：el-select，选项：5A、4A、3A、2A、1A、其他
4. 地址：el-input，必填，placeholder="请输入详细地址"
5. 开放时间：el-input，placeholder="如 08:00-18:00"
6. 门票价格：el-input，placeholder="如 免费 或 80元"
7. 联系电话：el-input，placeholder="请输入联系电话"
8. 景点描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
9. 景点详情：RichEditor 富文本编辑器，高度 300px
10. 浏览量：el-input-number，默认 0，范围 0-999999
11. 排序：el-input-number，默认 0，范围 0-999
12. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该景点吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：景点名称、封面图、地址必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 5 优先级：美食管理

> 重要性：畅游寿阳核心内容，前台首页有展示

**文件路径：** `src/views/Food/index.vue`

```
请帮我实现后台管理系统的美食管理页面，文件路径为 src/views/Food/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/food/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/food', formData)
- 修改：request.put('/admin/food', formData)
- 删除：request.delete(`/admin/food/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入美食名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 美食名称列：显示美食名称
   - 分类列：宽度 90，使用 el-tag 标签显示
   - 地址列：显示地址，超长省略
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增美食」或「编辑美食」
el-form 标签宽度 90px，字段如下：
1. 美食名称：el-input，必填，maxlength=50，placeholder="请输入美食名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 分类：el-select，选项：主食、小吃、菜肴、甜品、其他
4. 地址：el-input，placeholder="请输入店铺地址"
5. 价格：el-input，placeholder="如 15元"
6. 美食描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
7. 美食详情：RichEditor 富文本编辑器，高度 300px
8. 排序：el-input-number，默认 0，范围 0-999
9. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该美食吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：美食名称、封面图必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 6 优先级：酒店管理

> 重要性：畅游寿阳核心内容，前台首页有展示

**文件路径：** `src/views/Hotel/index.vue`

```
请帮我实现后台管理系统的酒店管理页面，文件路径为 src/views/Hotel/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/hotel/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/hotel', formData)
- 修改：request.put('/admin/hotel', formData)
- 删除：request.delete(`/admin/hotel/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入酒店名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 酒店名称列：显示酒店名称
   - 星级列：宽度 80，使用 el-rate 组件显示，disabled，星星颜色金色
   - 地址列：显示地址，超长省略
   - 价格区间列：宽度 120，显示价格区间文本
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增酒店」或「编辑酒店」
el-form 标签宽度 90px，字段如下：
1. 酒店名称：el-input，必填，maxlength=50，placeholder="请输入酒店名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 星级：el-rate，max=5，默认 3，提示"点击选择星级"
4. 地址：el-input，必填，placeholder="请输入酒店地址"
5. 联系电话：el-input，placeholder="请输入联系电话"
6. 价格区间：el-input，placeholder="如 200-500元/晚"
7. 酒店描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
8. 酒店详情：RichEditor 富文本编辑器，高度 300px
9. 排序：el-input-number，默认 0，范围 0-999
10. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该酒店吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：酒店名称、封面图、地址必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 7 优先级：场馆管理

> 重要性：文旅服务模块，演示可展示

**文件路径：** `src/views/Venue/index.vue`

```
请帮我实现后台管理系统的场馆管理页面，文件路径为 src/views/Venue/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/venue/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/venue', formData)
- 修改：request.put('/admin/venue', formData)
- 删除：request.delete(`/admin/venue/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入场馆名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 场馆名称列：显示场馆名称
   - 地址列：显示地址，超长省略
   - 开放时间列：宽度 140，显示开放时间
   - 容纳人数列：宽度 90，显示人数
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增场馆」或「编辑场馆」
el-form 标签宽度 90px，字段如下：
1. 场馆名称：el-input，必填，maxlength=50，placeholder="请输入场馆名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 地址：el-input，必填，placeholder="请输入场馆地址"
4. 开放时间：el-input，placeholder="如 09:00-21:00"
5. 容纳人数：el-input-number，默认 100，范围 0-99999
6. 联系电话：el-input，placeholder="请输入联系电话"
7. 场馆描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
8. 场馆详情：RichEditor 富文本编辑器，高度 300px
9. 排序：el-input-number，默认 0，范围 0-999
10. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该场馆吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：场馆名称、封面图、地址必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 8 优先级：票务管理

> 重要性：文旅服务模块，演示可展示

**文件路径：** `src/views/Ticket/index.vue`

```
请帮我实现后台管理系统的票务管理页面，文件路径为 src/views/Ticket/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/ticket/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/ticket', formData)
- 修改：request.put('/admin/ticket', formData)
- 删除：request.delete(`/admin/ticket/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入票务名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 票务名称列：显示票务名称
   - 价格列：宽度 100，红色高亮显示，如 ¥80.00
   - 原价列：宽度 100，灰色删除线显示
   - 库存列：宽度 80，显示库存数量
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增票务」或「编辑票务」
el-form 标签宽度 90px，字段如下：
1. 票务名称：el-input，必填，maxlength=50，placeholder="请输入票务名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 价格：el-input-number，必填，默认 0，最小值 0，精度 2，单位元
4. 原价：el-input-number，默认 0，最小值 0，精度 2，单位元，可选
5. 库存：el-input-number，默认 100，范围 0-99999
6. 使用说明：el-input type="textarea" :rows="3"，placeholder="请输入使用说明"
7. 购票须知：RichEditor 富文本编辑器，高度 300px
8. 排序：el-input-number，默认 0，范围 0-999
9. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该票务吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：票务名称、封面图、价格必填
5. 价格格式：显示时保留两位小数，前缀加 ¥ 符号
6. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 9 优先级：非遗管理

> 重要性：文旅时空模块，有特色标签，演示亮点

**文件路径：** `src/views/Heritage/index.vue`

```
请帮我实现后台管理系统的非遗管理页面，文件路径为 src/views/Heritage/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/heritage/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/heritage', formData)
- 修改：request.put('/admin/heritage', formData)
- 删除：request.delete(`/admin/heritage/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入非遗名称"）+ 级别下拉（全部/国家级/省级/市级/县级）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 非遗名称列：显示非遗名称
   - 级别列：宽度 90，使用 el-tag 标签显示，颜色规则：
     * 国家级：danger 红色
     * 省级：warning 橙色
     * 市级：primary 蓝色
     * 县级：success 绿色
   - 类别列：宽度 100，显示类别
   - 传承人类：宽度 100，显示传承人
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增非遗」或「编辑非遗」
el-form 标签宽度 90px，字段如下：
1. 非遗名称：el-input，必填，maxlength=50，placeholder="请输入非遗名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 级别：el-select，必填，选项：国家级、省级、市级、县级
4. 类别：el-input，placeholder="如 传统戏剧、传统美术"
5. 传承人：el-input，placeholder="请输入传承人姓名"
6. 非遗描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
7. 非遗详情：RichEditor 富文本编辑器，高度 300px
8. 排序：el-input-number，默认 0，范围 0-999
9. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该非遗项目吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：非遗名称、封面图、级别必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 10 优先级：文创管理

> 重要性：文旅时空模块，商品类，有价格库存

**文件路径：** `src/views/Product/index.vue`

```
请帮我实现后台管理系统的文创管理页面，文件路径为 src/views/Product/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/product/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/product', formData)
- 修改：request.put('/admin/product', formData)
- 删除：request.delete(`/admin/product/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入商品名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 商品名称列：显示商品名称
   - 价格列：宽度 100，红色高亮显示，如 ¥128.00
   - 原价列：宽度 100，灰色删除线显示
   - 库存列：宽度 80，显示库存数量
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增文创商品」或「编辑文创商品」
el-form 标签宽度 90px，字段如下：
1. 商品名称：el-input，必填，maxlength=50，placeholder="请输入商品名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 价格：el-input-number，必填，默认 0，最小值 0，精度 2，单位元
4. 原价：el-input-number，默认 0，最小值 0，精度 2，单位元，可选
5. 库存：el-input-number，默认 100，范围 0-99999
6. 商品描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
7. 商品详情：RichEditor 富文本编辑器，高度 300px
8. 排序：el-input-number，默认 0，范围 0-999
9. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该商品吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：商品名称、封面图、价格必填
5. 价格格式：显示时保留两位小数，前缀加 ¥ 符号
6. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 11 优先级：展馆管理

> 重要性：文旅时空模块，相对简单

**文件路径：** `src/views/Pavilion/index.vue`

```
请帮我实现后台管理系统的展馆管理页面，文件路径为 src/views/Pavilion/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/pavilion/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/pavilion', formData)
- 修改：request.put('/admin/pavilion', formData)
- 删除：request.delete(`/admin/pavilion/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入展馆名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 展馆名称列：显示展馆名称
   - 浏览量列：宽度 90，显示浏览量数字
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增数字展馆」或「编辑数字展馆」
el-form 标签宽度 90px，字段如下：
1. 展馆名称：el-input，必填，maxlength=50，placeholder="请输入展馆名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 展馆描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
4. 展馆详情：RichEditor 富文本编辑器，高度 300px
5. 浏览量：el-input-number，默认 0，范围 0-999999
6. 排序：el-input-number，默认 0，范围 0-999
7. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该展馆吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：展馆名称、封面图必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 12 优先级：攻略管理

> 重要性：畅游寿阳模块，相对简单

**文件路径：** `src/views/Guide/index.vue`

```
请帮我实现后台管理系统的攻略管理页面，文件路径为 src/views/Guide/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/guide/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/guide', formData)
- 修改：request.put('/admin/guide', formData)
- 删除：request.delete(`/admin/guide/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入攻略标题"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 攻略标题列：显示攻略标题
   - 作者列：宽度 100，显示作者
   - 天数列：宽度 80，显示"X天"
   - 浏览量列：宽度 90，显示浏览量数字
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 800px，标题根据操作类型显示「新增旅游攻略」或「编辑旅游攻略」
el-form 标签宽度 90px，字段如下：
1. 攻略标题：el-input，必填，maxlength=100，placeholder="请输入攻略标题"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 作者：el-input，必填，maxlength=20，placeholder="请输入作者"
4. 游玩天数：el-input-number，默认 3，范围 1-30，单位天
5. 攻略描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
6. 攻略详情：RichEditor 富文本编辑器，高度 300px
7. 浏览量：el-input-number，默认 0，范围 0-999999
8. 排序：el-input-number，默认 0，范围 0-999
9. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该攻略吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：攻略标题、封面图、作者必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

---

## 第 13 优先级：单位管理

> 重要性：辅助模块，相对简单

**文件路径：** `src/views/Org/index.vue`

```
请帮我实现后台管理系统的单位管理页面，文件路径为 src/views/Org/index.vue。

【技术栈】
Vue3 + 组合式 API + setup 语法糖 + Element Plus
请求封装：@/utils/request，已封装 get/post/put/del 方法，baseURL=/api

【参考模板】
参考 src/views/News/index.vue 的代码结构和风格，保持一致。

【API 接口】
- 列表：request.get('/admin/org/list', { params: { page, size, keyword, status } })
  返回 Page 对象：{ records: [], total: 100, size: 10, current: 1 }
- 新增：request.post('/admin/org', formData)
- 修改：request.put('/admin/org', formData)
- 删除：request.delete(`/admin/org/${id}`)

【页面结构】
1. 顶部搜索栏：关键词输入框（placeholder="请输入单位名称"）+ 状态下拉（全部/上架/下架）+ 搜索按钮 + 重置按钮 + 新增按钮（右侧）
2. 数据表格 el-table，列如下：
   - ID 列：宽度 60
   - 封面图列：宽度 80，显示 60x40 缩略图，el-image fit="cover"
   - 单位名称列：显示单位名称
   - 单位类型列：宽度 110，使用 el-tag 标签显示
   - 联系电话列：宽度 130，显示联系电话
   - 状态列：宽度 80，使用 el-switch，active-value=1，inactive-value=0
   - 创建时间列：宽度 160，格式化 YYYY-MM-DD HH:mm
   - 操作列：宽度 120，固定右侧，包含「编辑」「删除」两个按钮
3. 底部分页：el-pagination，布局 total, sizes, prev, pager, next, jumper
   - 每页条数选项：10, 20, 50

【新增/编辑弹窗】
el-dialog 宽度 700px，标题根据操作类型显示「新增文旅单位」或「编辑文旅单位」
el-form 标签宽度 90px，字段如下：
1. 单位名称：el-input，必填，maxlength=50，placeholder="请输入单位名称"
2. 封面图：UploadImage 组件，v-model 绑定 form.coverImage，必填
3. 单位类型：el-select，必填，选项：政府部门、事业单位、文化企业、其他
4. 地址：el-input，placeholder="请输入单位地址"
5. 联系电话：el-input，placeholder="请输入联系电话"
6. 单位描述：el-input type="textarea" :rows="3"，placeholder="请输入简短描述"
7. 排序：el-input-number，默认 0，范围 0-999
8. 状态：el-radio-group，选项：0=下架，1=上架，默认 1

【交互要求】
1. 状态切换：调用 put 接口更新状态，失败时 ElMessage 提示并回滚开关状态
2. 删除：点击删除弹出确认对话框"确认删除该单位吗？"，确认后调用删除接口
3. 编辑：点击编辑时获取详情数据，回填表单
4. 表单校验：单位名称、封面图、单位类型必填
5. 提交 loading：提交按钮在请求期间显示 loading
```

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

**需要更新的路由清单（按优先级排序）：**

| 优先级 | 模块 | 路由 path | 组件路径 |
|--------|------|-----------|---------|
| 1 | 轮播图管理 | banner | @/views/Banner/index.vue |
| 2 | 分类管理 | category | @/views/Category/index.vue |
| 3 | 活动管理 | activity | @/views/Activity/index.vue |
| 4 | 景点管理 | scenic | @/views/Scenic/index.vue |
| 5 | 美食管理 | food | @/views/Food/index.vue |
| 6 | 酒店管理 | hotel | @/views/Hotel/index.vue |
| 7 | 场馆管理 | venue | @/views/Venue/index.vue |
| 8 | 票务管理 | ticket | @/views/Ticket/index.vue |
| 9 | 非遗管理 | heritage | @/views/Heritage/index.vue |
| 10 | 文创管理 | product | @/views/Product/index.vue |
| 11 | 展馆管理 | pavilion | @/views/Pavilion/index.vue |
| 12 | 攻略管理 | guide | @/views/Guide/index.vue |
| 13 | 单位管理 | org | @/views/Org/index.vue |

---

## 完成验证清单

每完成一个模块，按以下清单逐项验证：

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

> **完成时间预估：** 每个模块约 15-20 分钟，13 个模块约需 4-5 小时。
> **毕设建议：** 至少完成第 1-5 优先级（约 1.5 小时），其余模块可在演示时说明"与资讯管理结构类似，已完成后端接口"。
