<template>
  <div class="news-list-page">
    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">文旅动态</h1>
        <p class="page-subtitle">了解寿阳文化旅游最新资讯</p>
      </div>

      <div class="page-content">
        <!-- 左侧主内容区 -->
        <div class="main-content">
          <!-- 分类标签筛选 -->
          <div class="category-tabs">
            <div class="tabs-scroll">
              <span
                class="tab-item"
                :class="{ active: !categoryId }"
                @click="handleCategoryChange(null)"
              >
                全部
              </span>
              <span
                v-for="cat in categoryList"
                :key="cat.id"
                class="tab-item"
                :class="{ active: categoryId === cat.id }"
                @click="handleCategoryChange(cat.id)"
              >
                {{ cat.name }}
              </span>
            </div>
          </div>

          <!-- 搜索框 -->
          <div class="search-bar">
            <el-input
              v-model="keyword"
              placeholder="搜索资讯标题..."
              clearable
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
              <template #append>
                <el-button @click="handleSearch">搜索</el-button>
              </template>
            </el-input>
          </div>

          <!-- 加载中骨架屏 -->
          <div v-if="loading" class="news-skeleton">
            <el-skeleton v-for="i in 5" :key="i" :rows="3" animated class="skeleton-item" />
          </div>

          <!-- 资讯列表 -->
          <div v-else-if="newsList.length > 0" class="news-list">
            <NewsCard
              v-for="news in newsList"
              :key="news.id"
              :news="news"
              :category-name="news.categoryName"
            />
          </div>

          <!-- 空状态 -->
          <el-empty v-else description="暂无相关资讯" class="empty-state" />

          <!-- 分页 -->
          <Pagination
            v-if="total > 0"
            v-model:page="currentPage"
            :total="total"
            :page-size="pageSize"
            @change="handlePageChange"
          />
        </div>

        <!-- 右侧侧边栏 -->
        <div class="sidebar">
          <!-- 热门资讯排行 -->
          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <el-icon color="#e57373"><HotWater /></el-icon>
              热门资讯
            </h3>
            <div class="hot-list">
              <div
                v-for="(item, index) in hotNews"
                :key="item.id"
                class="hot-item"
                @click="goDetail(item.id)"
              >
                <span class="hot-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="hot-title">{{ item.title }}</span>
              </div>
            </div>
          </div>

          <!-- 分类导航 -->
          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <el-icon color="#ffa726"><Menu /></el-icon>
              资讯分类
            </h3>
            <div class="category-nav">
              <span
                class="nav-item"
                :class="{ active: !categoryId }"
                @click="handleCategoryChange(null)"
              >
                全部资讯
              </span>
              <span
                v-for="cat in categoryList"
                :key="cat.id"
                class="nav-item"
                :class="{ active: categoryId === cat.id }"
                @click="handleCategoryChange(cat.id)"
              >
                {{ cat.name }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, HotWater, Menu } from '@element-plus/icons-vue'
import NewsCard from '@/components/NewsCard.vue'
import Pagination from '@/components/Pagination.vue'
import { getNewsList, getNewsCategory, getHotNews } from '@/api/news'

const route = useRoute()
const router = useRouter()

// 状态
const loading = ref(true)
const newsList = ref([])
const categoryList = ref([])
const hotNews = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const categoryId = ref(null)
const keyword = ref('')

// 从 URL 读取初始参数
const initFromUrl = () => {
  if (route.query.category) {
    categoryId.value = Number(route.query.category) || null
  }
  if (route.query.page) {
    currentPage.value = Number(route.query.page) || 1
  }
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }
}

// 加载资讯列表
const loadNewsList = async () => {
  loading.value = true
  try {
    const res = await getNewsList({
      page: currentPage.value,
      size: pageSize.value,
      categoryId: categoryId.value,
      keyword: keyword.value || undefined
    })
    newsList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载资讯列表失败:', e)
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategoryList = async () => {
  try {
    const res = await getNewsCategory()
    categoryList.value = res.data || []
  } catch (e) {
    console.error('加载分类列表失败:', e)
  }
}

// 加载热门资讯
const loadHotNews = async () => {
  try {
    const res = await getHotNews()
    hotNews.value = res.data || []
  } catch (e) {
    console.error('加载热门资讯失败:', e)
  }
}

// 更新 URL 查询参数
const updateUrl = () => {
  const query = {}
  if (categoryId.value) query.category = categoryId.value
  if (currentPage.value > 1) query.page = currentPage.value
  if (keyword.value) query.keyword = keyword.value
  router.replace({ path: '/news', query })
}

// 分类切换
const handleCategoryChange = (id) => {
  categoryId.value = id
  currentPage.value = 1
  updateUrl()
  loadNewsList()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  updateUrl()
  loadNewsList()
}

// 翻页
const handlePageChange = ({ page }) => {
  currentPage.value = page
  updateUrl()
  loadNewsList()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/news/${id}`)
}

// 监听路由变化（浏览器前进后退）
watch(() => route.query, () => {
  initFromUrl()
  loadNewsList()
})

onMounted(() => {
  initFromUrl()
  loadCategoryList()
  loadHotNews()
  loadNewsList()
})
</script>

<style lang="scss" scoped>
.news-list-page {
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

/* 内容布局 */
.page-content {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.main-content {
  flex: 1;
  min-width: 0;
}

/* 分类标签 */
.category-tabs {
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.tabs-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.tab-item {
  flex-shrink: 0;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  color: #607d8b;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;

  &:hover {
    background: #f0f4fa;
    color: #2c3e6b;
  }

  &.active {
    background: #2c3e6b;
    color: #fff;
  }
}

/* 搜索框 */
.search-bar {
  margin-bottom: 20px;

  :deep(.el-input__wrapper) {
    border-radius: 8px 0 0 8px;
  }

  :deep(.el-input-group__append) {
    border-radius: 0 8px 8px 0;
    background: #2c3e6b;
    color: #fff;
    border-color: #2c3e6b;

    .el-button {
      background: transparent;
      border: none;
      color: #fff;
    }
  }
}

/* 资讯列表 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 骨架屏 */
.news-skeleton {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-item {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 10px;
  padding: 60px 0;
}

/* 侧边栏 */
.sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 热门列表 */
.hot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 0;

  &:hover .hot-title {
    color: #2c3e6b;
  }
}

.hot-rank {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border-radius: 4px;
  background: #eceff1;
  color: #78909c;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;

  &.top {
    background: #e57373;
    color: #fff;
  }
}

.hot-title {
  font-size: 13px;
  color: #455a64;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

/* 分类导航 */
.category-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #607d8b;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f0f4fa;
    color: #2c3e6b;
  }

  &.active {
    background: #e8eef7;
    color: #2c3e6b;
    font-weight: 600;
  }
}

/* 响应式 */
@media (max-width: 992px) {
  .page-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .sidebar-card {
    flex: 1;
    min-width: 280px;
  }
}

@media (max-width: 576px) {
  .page-title {
    font-size: 22px;
  }

  .sidebar {
    flex-direction: column;
  }
}
</style>
