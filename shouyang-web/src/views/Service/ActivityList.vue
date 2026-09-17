<template>
  <div class="activity-list-page">
    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">活动报名</h1>
        <p class="page-subtitle">参与精彩文旅活动，丰富您的文化生活</p>
      </div>

      <!-- 状态筛选 -->
      <div class="filter-bar">
        <span
          v-for="tab in statusTabs"
          :key="tab.value"
          class="filter-item"
          :class="{ active: statusFilter === tab.value }"
          @click="handleStatusChange(tab.value)"
        >
          {{ tab.label }}
        </span>
      </div>

      <!-- 加载中骨架屏 -->
      <div v-if="loading" class="activity-grid">
        <el-skeleton v-for="i in 6" :key="i" :rows="4" animated class="skeleton-card" />
      </div>

      <!-- 活动卡片列表 -->
      <div v-else-if="activityList.length > 0" class="activity-grid">
        <div
          v-for="activity in activityList"
          :key="activity.id"
          class="activity-card"
          @click="goDetail(activity.id)"
        >
          <!-- 封面图 -->
          <div class="card-cover">
            <el-image
              :src="activity.coverImage || defaultCover"
              fit="cover"
              class="cover-img"
              :lazy="true"
            >
              <template #error>
                <div class="cover-placeholder">
                  <el-icon :size="32" color="#fff"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <!-- 状态标签 -->
            <span class="status-tag" :class="`status-${getStatus(activity)}`">
              {{ getStatusText(activity) }}
            </span>
          </div>

          <!-- 内容区 -->
          <div class="card-body">
            <h4 class="card-title">{{ activity.title }}</h4>

            <div class="card-info">
              <span class="info-item">
                <el-icon><Clock /></el-icon>
                {{ formatDateTime(activity.startTime) }}
              </span>
              <span class="info-item">
                <el-icon><Location /></el-icon>
                {{ activity.venue || '待定' }}
              </span>
            </div>

            <!-- 报名进度 -->
            <div class="signup-progress">
              <div class="progress-info">
                <span>已报名 {{ activity.signupCount || 0 }}</span>
                <span v-if="activity.maxPeople > 0">/ {{ activity.maxPeople }} 人</span>
                <span v-else>不限人数</span>
              </div>
              <el-progress
                :percentage="getProgress(activity)"
                :stroke-width="6"
                :show-text="false"
                :color="getProgressColor(activity)"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无活动" class="empty-state" />

      <!-- 分页 -->
      <Pagination
        v-if="total > 0"
        v-model:page="currentPage"
        :total="total"
        :page-size="pageSize"
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Clock, Location, Picture } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getActivityList } from '@/api/service'

const router = useRouter()

// 状态
const loading = ref(true)
const activityList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(9)
const statusFilter = ref(null)

// 默认占位图
const defaultCover = 'https://picsum.photos/seed/activity-cover/400/240'

// 状态筛选标签
const statusTabs = [
  { label: '全部', value: null },
  { label: '未开始', value: 0 },
  { label: '报名中', value: 1 },
  { label: '已结束', value: 2 }
]

// 获取活动状态（优先用后端 status，没有则根据时间计算）
const getStatus = (activity) => {
  if (activity.status != null) return activity.status
  // 根据时间计算
  const now = new Date()
  const start = new Date(activity.startTime)
  const end = new Date(activity.endTime)
  if (now < start) return 0
  if (now > end) return 2
  return 1
}

// 状态文字
const getStatusText = (activity) => {
  const status = getStatus(activity)
  const map = { 0: '未开始', 1: '报名中', 2: '已结束' }
  return map[status] || '未知'
}

// 报名进度百分比
const getProgress = (activity) => {
  if (!activity.maxPeople || activity.maxPeople <= 0) return 0
  const count = activity.signupCount || 0
  return Math.min(100, Math.round((count / activity.maxPeople) * 100))
}

// 进度条颜色
const getProgressColor = (activity) => {
  const progress = getProgress(activity)
  if (progress >= 100) return '#f56c6c'
  if (progress >= 80) return '#e6a23c'
  return '#67c23a'
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hour}:${minute}`
}

// 加载活动列表
const loadList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (statusFilter.value != null) {
      params.status = statusFilter.value
    }
    const res = await getActivityList(params)
    activityList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载活动列表失败:', e)
  } finally {
    loading.value = false
  }
}

// 状态筛选切换
const handleStatusChange = (value) => {
  statusFilter.value = value
  currentPage.value = 1
  loadList()
}

// 翻页
const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/service/activity/${id}`)
}

onMounted(() => {
  loadList()
})
</script>

<style lang="scss" scoped>
.activity-list-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a2a4a;
  margin: 0 0 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #78909c;
  margin: 0;
}

/* 筛选栏 */
.filter-bar {
  background: #fff;
  border-radius: 10px;
  padding: 14px 20px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-item {
  padding: 6px 18px;
  border-radius: 20px;
  font-size: 13px;
  color: #607d8b;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f0f4fa;
    color: #2c3e6b;
  }

  &.active {
    background: #2c3e6b;
    color: #fff;
  }
}

/* 活动卡片网格 */
.activity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.skeleton-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  height: 320px;
}

/* 活动卡片 */
.activity-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
    border-color: transparent;

    .cover-img {
      transform: scale(1.05);
    }

    .card-title {
      color: #2c3e6b;
    }
  }
}

/* 封面图 */
.card-cover {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #e0e6ed;
}

.cover-img {
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
}

.cover-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 状态标签 */
.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  backdrop-filter: blur(4px);

  &.status-0 {
    background: rgba(64, 158, 255, 0.9);
  }

  &.status-1 {
    background: rgba(103, 194, 58, 0.9);
  }

  &.status-2 {
    background: rgba(144, 147, 153, 0.9);
  }
}

/* 内容区 */
.card-body {
  padding: 16px 18px 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #90a4ae;
}

/* 报名进度 */
.signup-progress {
  margin-top: auto;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #78909c;
  margin-bottom: 6px;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 10px;
  padding: 60px 0;
  margin-bottom: 24px;
}

/* 响应式 */
@media (max-width: 992px) {
  .activity-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .activity-grid {
    grid-template-columns: 1fr;
  }
}
</style>
