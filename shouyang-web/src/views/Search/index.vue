<template>
  <div class="search-page">
    <div class="search-container">
      <!-- 搜索框 -->
      <div class="search-header">
        <div class="search-title">全站搜索</div>
        <div class="search-box">
          <el-input
            ref="searchInputRef"
            v-model="keyword"
            placeholder="请输入搜索关键词"
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="handleSearch">
            搜索
          </el-button>
        </div>
      </div>

      <!-- 搜索结果统计 -->
      <div v-if="keyword" class="result-stats">
        找到 <span class="highlight">{{ total }}</span> 条与 "<span class="highlight">{{ keyword }}</span>" 相关的结果
      </div>

      <!-- 分类 Tab -->
      <div v-if="keyword" class="type-tabs">
        <el-radio-group v-model="currentType" @change="handleTypeChange">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="news">资讯</el-radio-button>
          <el-radio-button value="scenic">景点</el-radio-button>
          <el-radio-button value="food">美食</el-radio-button>
          <el-radio-button value="hotel">酒店</el-radio-button>
          <el-radio-button value="activity">活动</el-radio-button>
          <el-radio-button value="product">文创</el-radio-button>
          <el-radio-button value="heritage">非遗</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 搜索结果列表 -->
      <div v-if="keyword" class="result-list" v-loading="loading">
        <div
          v-for="item in resultList"
          :key="item.type + '-' + item.id"
          class="result-item"
          @click="handleItemClick(item)"
        >
          <div class="item-cover" v-if="item.coverImage">
            <el-image :src="item.coverImage" fit="cover" class="cover-img" />
          </div>
          <div class="item-content">
            <div class="item-header">
              <el-tag :type="getTagType(item.type)" size="small" effect="plain">
                {{ item.typeName }}
              </el-tag>
              <span class="item-time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="item-title" v-html="highlightText(item.title)"></div>
            <div class="item-summary" v-if="item.summary" v-html="highlightText(item.summary)"></div>
          </div>
        </div>

        <!-- 空结果 -->
        <el-empty
          v-if="!loading && resultList.length === 0"
          description="未找到相关内容，换个关键词试试吧"
          :image-size="120"
        />
      </div>

      <!-- 分页 -->
      <div v-if="keyword && total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @size-change="handlePageChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { searchAll } from '@/api/search'

const route = useRoute()
const router = useRouter()

const searchInputRef = ref(null)
const loading = ref(false)
const keyword = ref('')
const currentType = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const resultList = ref([])

// 类型标签颜色映射
const typeTagMap = {
  news: '',
  scenic: 'success',
  food: 'warning',
  hotel: 'info',
  activity: 'danger',
  product: 'warning',
  heritage: 'danger',
  pavilion: 'info',
  guide: 'success'
}

const getTagType = (type) => {
  return typeTagMap[type] || ''
}

// 关键词高亮
const highlightText = (text) => {
  if (!text || !keyword.value) return text
  const reg = new RegExp(`(${keyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi')
  return text.replace(reg, '<span style="color: #f56c6c; font-weight: 600;">$1</span>')
}

// 搜索
const doSearch = async () => {
  if (!keyword.value.trim()) {
    resultList.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    const res = await searchAll({
      keyword: keyword.value,
      type: currentType.value,
      page: currentPage.value,
      size: pageSize.value
    })
    resultList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('搜索失败:', e)
  } finally {
    loading.value = false
  }
}

// 点击搜索按钮
const handleSearch = () => {
  currentPage.value = 1
  updateUrl()
  doSearch()
}

// 切换类型
const handleTypeChange = () => {
  currentPage.value = 1
  updateUrl()
  doSearch()
}

// 翻页
const handlePageChange = () => {
  updateUrl()
  doSearch()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 更新 URL 参数
const updateUrl = () => {
  router.replace({
    path: '/search',
    query: {
      keyword: keyword.value,
      type: currentType.value,
      page: currentPage.value
    }
  })
}

// 点击搜索结果
const handleItemClick = (item) => {
  const routeMap = {
    news: `/news/${item.id}`,
    scenic: `/travel/scenic/${item.id}`,
    food: `/travel/food/${item.id}`,
    hotel: `/travel/hotel/${item.id}`,
    activity: `/service/activity/${item.id}`,
    product: `/culture/product/${item.id}`,
    heritage: `/culture/heritage/${item.id}`,
    pavilion: `/culture/pavilion/${item.id}`,
    guide: `/travel/guide/${item.id}`
  }
  const path = routeMap[item.type]
  if (path) {
    router.push(path)
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 10)
}

// 从 URL 初始化参数
const initFromUrl = () => {
  keyword.value = route.query.keyword || ''
  currentType.value = route.query.type || 'all'
  currentPage.value = parseInt(route.query.page) || 1
}

// 监听路由变化
watch(() => route.query, () => {
  initFromUrl()
  if (keyword.value) {
    doSearch()
  }
}, { deep: true })

onMounted(() => {
  initFromUrl()
  if (keyword.value) {
    doSearch()
  }
  // 搜索框自动聚焦
  nextTick(() => {
    searchInputRef.value?.focus()
  })
})
</script>

<style lang="scss" scoped>
.search-page {
  min-height: 60vh;
  background: #f5f7fa;
  padding: 24px 0;
}
.search-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 16px;
}
.search-header {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.search-title {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e6b;
  text-align: center;
  margin-bottom: 20px;
}
.search-box {
  display: flex;
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
  :deep(.el-input__wrapper) {
    border-radius: 8px;
  }
}
.result-stats {
  font-size: 14px;
  color: #606266;
  margin-bottom: 16px;
  .highlight {
    color: #2c3e6b;
    font-weight: 600;
  }
}
.type-tabs {
  margin-bottom: 16px;
  :deep(.el-radio-button__inner) {
    padding: 8px 16px;
  }
}
.result-list {
  background: #fff;
  border-radius: 12px;
  padding: 8px 24px;
  min-height: 300px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.result-item {
  display: flex;
  gap: 16px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: background 0.2s;
  &:last-child {
    border-bottom: none;
  }
  &:hover {
    background: #fafafa;
  }
}
.item-cover {
  flex-shrink: 0;
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  .cover-img {
    width: 100%;
    height: 100%;
  }
}
.item-content {
  flex: 1;
  min-width: 0;
}
.item-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.item-time {
  font-size: 12px;
  color: #909399;
}
.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-summary {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
