<template>
  <div class="activity-detail-page">
    <div class="page-container">
      <!-- 面包屑导航 -->
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/service/activity' }">活动报名</el-breadcrumb-item>
          <el-breadcrumb-item>详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="detail-skeleton">
        <el-skeleton :rows="15" animated />
      </div>

      <template v-else-if="activity">
        <div class="detail-layout">
          <!-- 主内容区 -->
          <div class="main-content">
            <!-- 封面图 -->
            <div class="detail-cover" v-if="activity.coverImage">
              <el-image :src="activity.coverImage" fit="cover" class="cover-img" />
              <div class="cover-overlay">
                <span class="status-tag" :class="`status-${activityStatus}`">
                  {{ statusText }}
                </span>
                <h1 class="cover-title">{{ activity.title }}</h1>
              </div>
            </div>

            <!-- 无封面时的标题 -->
            <div v-else class="detail-header-plain">
              <span class="status-tag" :class="`status-${activityStatus}`">
                {{ statusText }}
              </span>
              <h1 class="detail-title">{{ activity.title }}</h1>
            </div>

            <!-- 活动信息卡片 -->
            <div class="info-card">
              <h3 class="info-title">
                <el-icon color="#2c3e6b"><InfoFilled /></el-icon>
                活动信息
              </h3>
              <el-descriptions :column="2" border size="default">
                <el-descriptions-item label="活动时间">
                  {{ formatDateTime(activity.startTime) }} ~ {{ formatDateTime(activity.endTime) }}
                </el-descriptions-item>
                <el-descriptions-item label="活动地点">
                  {{ activity.venue || '待定' }}
                </el-descriptions-item>
                <el-descriptions-item label="报名截止">
                  {{ formatDateTime(activity.signupDeadline) }}
                </el-descriptions-item>
                <el-descriptions-item label="人数限制">
                  {{ activity.maxPeople > 0 ? activity.maxPeople + ' 人' : '不限人数' }}
                </el-descriptions-item>
              </el-descriptions>

              <!-- 报名进度 -->
              <div class="signup-section">
                <div class="signup-header">
                  <span class="signup-label">报名进度</span>
                  <span class="signup-count">
                    已报名 <strong>{{ activity.signupCount || 0 }}</strong>
                    <template v-if="activity.maxPeople > 0"> / {{ activity.maxPeople }} 人</template>
                    <template v-else>（不限人数）</template>
                  </span>
                </div>
                <el-progress
                  :percentage="signupProgress"
                  :stroke-width="10"
                  :color="progressColor"
                />
              </div>
            </div>

            <!-- 活动详情 -->
            <div class="content-card">
              <h3 class="content-title">
                <el-icon color="#2c3e6b"><Document /></el-icon>
                活动详情
              </h3>
              <div class="article-content" v-html="activity.description || '<p>暂无详细介绍</p>'"></div>
            </div>
          </div>

          <!-- 侧边栏：报名操作 -->
          <div class="detail-sidebar">
            <div class="sidebar-card signup-card">
              <h3 class="sidebar-title">立即报名</h3>

              <!-- 报名状态展示 -->
              <div class="signup-status">
                <div class="status-row">
                  <span class="status-label">活动状态</span>
                  <span class="status-value" :class="`text-${activityStatus}`">{{ statusText }}</span>
                </div>
                <div class="status-row">
                  <span class="status-label">已报名</span>
                  <span class="status-value">{{ activity.signupCount || 0 }} 人</span>
                </div>
                <div class="status-row" v-if="activity.maxPeople > 0">
                  <span class="status-label">剩余名额</span>
                  <span class="status-value" :class="{ 'text-full': remaining <= 0 }">
                    {{ remaining > 0 ? remaining + ' 个' : '已满' }}
                  </span>
                </div>
              </div>

              <!-- 报名按钮 -->
              <el-button
                type="primary"
                size="large"
                class="signup-btn"
                :disabled="!canRegister"
                @click="handleRegisterClick"
              >
                {{ buttonText }}
              </el-button>

              <p class="signup-tip" v-if="!canRegister && !isRegistered">
                {{ buttonTip }}
              </p>
              <p class="signup-tip success" v-if="isRegistered">
                <el-icon><CircleCheck /></el-icon>
                您已成功报名该活动
              </p>
            </div>

            <!-- 返回列表 -->
            <div class="sidebar-card">
              <el-button type="primary" plain @click="$router.back()" class="back-btn">
                <el-icon><ArrowLeft /></el-icon>
                返回活动列表
              </el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 不存在 -->
      <el-empty v-else description="活动不存在" class="empty-state" />
    </div>

    <!-- 报名表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="活动报名"
      width="480px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="活动名称">
          <span class="form-static">{{ activity?.title }}</span>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入您的姓名" maxlength="20" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="选填，如有特殊需求请备注"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确认报名</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  InfoFilled, Document, ArrowLeft, CircleCheck
} from '@element-plus/icons-vue'
import { getActivityDetail, registerActivity } from '@/api/service'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 状态
const loading = ref(true)
const activity = ref(null)
const isRegistered = ref(false)

// 报名对话框
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = ref({
  name: '',
  phone: '',
  remark: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// 活动状态（优先用后端 status，没有则根据时间计算）
const activityStatus = computed(() => {
  if (!activity.value) return 0
  if (activity.value.status != null) return activity.value.status
  const now = new Date()
  const start = new Date(activity.value.startTime)
  const end = new Date(activity.value.endTime)
  if (now < start) return 0
  if (now > end) return 2
  return 1
})

// 状态文字
const statusText = computed(() => {
  const map = { 0: '未开始', 1: '报名中', 2: '已结束' }
  return map[activityStatus.value] || '未知'
})

// 报名进度
const signupProgress = computed(() => {
  if (!activity.value || !activity.value.maxPeople || activity.value.maxPeople <= 0) return 0
  const count = activity.value.signupCount || 0
  return Math.min(100, Math.round((count / activity.value.maxPeople) * 100))
})

// 进度条颜色
const progressColor = computed(() => {
  if (signupProgress.value >= 100) return '#f56c6c'
  if (signupProgress.value >= 80) return '#e6a23c'
  return '#67c23a'
})

// 剩余名额
const remaining = computed(() => {
  if (!activity.value || !activity.value.maxPeople || activity.value.maxPeople <= 0) return 999
  return activity.value.maxPeople - (activity.value.signupCount || 0)
})

// 是否可以报名
const canRegister = computed(() => {
  if (!activity.value) return false
  if (isRegistered.value) return false // 已报名
  if (activityStatus.value === 2) return false // 已结束
  if (activity.value.maxPeople > 0 && remaining.value <= 0) return false // 已满
  // 检查报名截止时间
  if (activity.value.signupDeadline) {
    const now = new Date()
    const deadline = new Date(activity.value.signupDeadline)
    if (now > deadline) return false
  }
  return true
})

// 按钮文字
const buttonText = computed(() => {
  if (isRegistered.value) return '已报名'
  if (activityStatus.value === 2) return '已结束'
  if (activity.value?.maxPeople > 0 && remaining.value <= 0) return '报名已满'
  return '立即报名'
})

// 按钮提示
const buttonTip = computed(() => {
  if (activityStatus.value === 2) return '该活动已结束，无法报名'
  if (activity.value?.maxPeople > 0 && remaining.value <= 0) return '报名人数已满，请关注后续活动'
  return ''
})

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 加载详情
const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getActivityDetail(id)
    activity.value = res.data?.activity
    isRegistered.value = res.data?.isRegistered || false
  } catch (e) {
    console.error('加载活动详情失败:', e)
  } finally {
    loading.value = false
  }
}

// 点击报名按钮
const handleRegisterClick = () => {
  // 检查登录状态
  if (!userStore.isLogin) {
    ElMessageBox.confirm('报名需要先登录，是否前往登录？', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      router.push({ path: '/login', query: { redirect: route.fullPath } })
    }).catch(() => {})
    return
  }
  // 打开报名对话框
  dialogVisible.value = true
}

// 提交报名
const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    await registerActivity({
      activityId: activity.value.id,
      name: form.value.name,
      phone: form.value.phone,
      remark: form.value.remark
    })
    ElMessage.success('报名成功！')
    dialogVisible.value = false
    // 刷新状态
    isRegistered.value = true
    if (activity.value) {
      activity.value.signupCount = (activity.value.signupCount || 0) + 1
    }
  } catch (e) {
    // 错误信息已由全局拦截器处理
    console.error('报名失败:', e)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.value = { name: '', phone: '', remark: '' }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style lang="scss" scoped>
.activity-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}

/* 面包屑 */
.breadcrumb-bar {
  margin-bottom: 20px;

  :deep(.el-breadcrumb__inner) {
    color: #78909c;
    font-size: 13px;
  }

  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: #2c3e6b;
    font-weight: 500;
  }
}

/* 布局 */
.detail-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.main-content {
  flex: 1;
  min-width: 0;
}

/* 封面图 */
.detail-cover {
  position: relative;
  width: 100%;
  height: 320px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;

  .cover-img {
    width: 100%;
    height: 100%;
  }
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.75), transparent 60%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 30px;
  gap: 12px;
}

.cover-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

/* 无封面时的标题 */
.detail-header-plain {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a2a4a;
  margin: 0;
}

/* 状态标签 */
.status-tag {
  align-self: flex-start;
  padding: 4px 14px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;

  &.status-0 {
    background: #409eff;
  }

  &.status-1 {
    background: #67c23a;
  }

  &.status-2 {
    background: #909399;
  }
}

/* 信息卡片 */
.info-card,
.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 30px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.info-title,
.content-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 报名进度 */
.signup-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eceff1;
}

.signup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.signup-label {
  font-size: 14px;
  font-weight: 600;
  color: #1a2a4a;
}

.signup-count {
  font-size: 13px;
  color: #78909c;

  strong {
    color: #2c3e6b;
    font-size: 16px;
  }
}

/* 富文本正文 */
.article-content {
  font-size: 15px;
  line-height: 1.9;
  color: #37474f;
  word-break: break-word;

  :deep(h1), :deep(h2), :deep(h3) {
    color: #1a2a4a;
    margin: 24px 0 12px;
    font-weight: 600;
  }

  :deep(p) {
    margin: 0 0 16px;
    text-indent: 2em;
  }

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
    margin: 16px auto;
    display: block;
  }

  :deep(ul), :deep(ol) {
    margin: 0 0 16px;
    padding-left: 2em;
  }
}

/* 侧边栏 */
.detail-sidebar {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: sticky;
  top: 20px;
}

.sidebar-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 20px;
}

/* 报名状态 */
.signup-status {
  margin-bottom: 20px;
}

.status-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f2f5;

  &:last-child {
    border-bottom: none;
  }
}

.status-label {
  font-size: 13px;
  color: #78909c;
}

.status-value {
  font-size: 14px;
  font-weight: 500;
  color: #1a2a4a;

  &.text-0 { color: #409eff; }
  &.text-1 { color: #67c23a; }
  &.text-2 { color: #909399; }
  &.text-full { color: #f56c6c; }
}

/* 报名按钮 */
.signup-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
}

.signup-tip {
  margin: 12px 0 0;
  font-size: 12px;
  color: #909399;
  text-align: center;

  &.success {
    color: #67c23a;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
  }
}

/* 返回按钮 */
.back-btn {
  width: 100%;
}

/* 表单静态文字 */
.form-static {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* 骨架屏 */
.detail-skeleton {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 80px 0;
}

/* 响应式 */
@media (max-width: 992px) {
  .detail-layout {
    flex-direction: column;
  }

  .detail-sidebar {
    width: 100%;
    position: static;
  }

  .detail-cover {
    height: 240px;
  }

  .cover-title {
    font-size: 22px;
  }
}
</style>
