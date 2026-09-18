<template>
  <div class="org-list-page">
    <div class="page-container">
      <!-- 面包屑导航 -->
      <div class="breadcrumb-bar">
        <el-breadcrumb separator=">">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>文旅单位</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="filter-item">
          <span class="filter-label">类型：</span>
          <div class="type-tabs">
            <span
              v-for="type in typeOptions"
              :key="type.value"
              class="type-tab"
              :class="{ active: activeType === type.value }"
              @click="handleTypeChange(type.value)"
            >
              {{ type.label }}
            </span>
          </div>
        </div>
      </div>

      <!-- 单位卡片网格 -->
      <div v-loading="loading" class="org-grid">
        <div
          v-for="item in orgList"
          :key="item.id"
          class="org-card"
          @click="goDetail(item.id)"
        >
          <div class="org-cover">
            <el-image
              :src="item.coverImage || defaultCover"
              fit="cover"
              class="cover-img"
              :lazy="true"
            >
              <template #error>
                <div class="cover-placeholder">
                  <el-icon :size="40"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          <div class="org-name">{{ item.name }}</div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && orgList.length === 0" description="暂无文旅单位" />
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadOrgList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import { getOrgList } from '@/api/org'

const router = useRouter()

// 状态
const loading = ref(false)
const orgList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(12)
const activeType = ref('')

// 类型选项
const typeOptions = [
  { label: '全部', value: '' },
  { label: '图书馆', value: '图书馆' },
  { label: '文化馆', value: '文化馆' },
  { label: '文化站', value: '文化站' },
  { label: '文化中心', value: '文化中心' }
]

// 默认占位图
const defaultCover = 'https://picsum.photos/seed/org/400/300'

// 加载单位列表
const loadOrgList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      status: 1
    }
    // 按类型筛选（如果有）
    if (activeType.value) {
      params.type = activeType.value
    }
    const res = await getOrgList(params)
    orgList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error('加载文旅单位失败:', e)
  } finally {
    loading.value = false
  }
}

// 类型切换
const handleTypeChange = (type) => {
  activeType.value = type
  page.value = 1
  loadOrgList()
}

// 跳转详情
const goDetail = (id) => {
  // 暂时只跳转，后续做详情页
  console.log('查看单位详情:', id)
}

onMounted(() => {
  loadOrgList()
})
</script>

<style lang="scss" scoped>
.org-list-page {
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
  text-align: right;

  :deep(.el-breadcrumb__inner) {
    color: #90a4ae;
    font-size: 13px;
  }
}

/* 筛选栏 */
.filter-bar {
  background: #fff;
  border-radius: 8px;
  padding: 20px 24px;
  margin-bottom: 24px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-label {
  font-size: 14px;
  color: #607d8b;
  margin-right: 16px;
}

.type-tabs {
  display: flex;
  gap: 24px;
}

.type-tab {
  font-size: 14px;
  color: #607d8b;
  cursor: pointer;
  transition: color 0.2s;
  padding: 4px 0;

  &:hover {
    color: #2c3e6b;
  }

  &.active {
    color: #2c3e6b;
    font-weight: 600;
  }
}

/* 单位卡片网格 */
.org-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  min-height: 400px;
}

.org-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

    .cover-img {
      transform: scale(1.05);
    }
  }
}

.org-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
    transition: transform 0.3s;
  }
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: #eceff1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #b0bec5;
}

.org-name {
  padding: 14px 16px;
  font-size: 15px;
  font-weight: 500;
  color: #37474f;
  text-align: center;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 992px) {
  .org-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .org-grid {
    grid-template-columns: 1fr;
  }

  .type-tabs {
    gap: 16px;
    flex-wrap: wrap;
  }
}
</style>
