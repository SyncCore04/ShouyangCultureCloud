<template>
  <div class="news-detail-page">
    <div class="page-container">
      <!-- 面包屑导航 -->
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/news' }">文旅动态</el-breadcrumb-item>
          <el-breadcrumb-item v-if="newsDetail?.categoryName">{{ newsDetail?.categoryName }}</el-breadcrumb-item>
          <el-breadcrumb-item>详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="page-content">
        <!-- 左侧主内容区 -->
        <div class="main-content">
          <!-- 加载中 -->
          <div v-if="loading" class="detail-skeleton">
            <el-skeleton :rows="10" animated />
          </div>

          <template v-else-if="newsDetail">
            <!-- 文章头部 -->
            <article class="article-card">
              <header class="article-header">
                <h1 class="article-title">{{ newsDetail.title }}</h1>
                <div class="article-meta">
                  <span class="meta-item">
                    <el-icon><User /></el-icon>
                    {{ newsDetail.author || '寿阳文旅云' }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>
                    {{ formatDate(newsDetail.createTime) }}
                  </span>
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ newsDetail.viewCount || 0 }} 阅读
                  </span>
                  <span v-if="newsDetail.categoryName" class="meta-tag">
                    {{ newsDetail.categoryName }}
                  </span>
                  <!-- 收藏按钮 -->
                  <span class="favorite-btn" :class="{ active: isFavorited }" @click="handleFavorite">
                    <el-icon><Star /></el-icon>
                    {{ isFavorited ? '已收藏' : '收藏' }}
                  </span>
                </div>
              </header>

              <!-- 封面图 -->
              <div v-if="newsDetail.coverImage" class="article-cover">
                <el-image :src="newsDetail.coverImage" fit="cover" class="cover-img" />
              </div>

              <!-- 正文（富文本） -->
              <div class="article-content" v-html="newsDetail.content || '<p>暂无内容</p>'"></div>

              <!-- 文章底部 -->
              <footer class="article-footer">
                <div class="footer-tip">
                  <el-icon><InfoFilled /></el-icon>
                  本文由寿阳文旅云发布，转载请注明出处
                </div>
              </footer>
            </article>

            <!-- 上一篇/下一篇 -->
            <div class="prev-next-nav">
              <div class="nav-item prev" v-if="prevNews" @click="goDetail(prevNews.id)">
                <span class="nav-label">上一篇</span>
                <span class="nav-title">{{ prevNews.title }}</span>
              </div>
              <div class="nav-item next" v-if="nextNews" @click="goDetail(nextNews.id)">
                <span class="nav-label">下一篇</span>
                <span class="nav-title">{{ nextNews.title }}</span>
              </div>
            </div>
          </template>

          <!-- 文章不存在 -->
          <el-empty v-else description="资讯不存在或已下架" class="empty-state" />
        </div>

        <!-- 右侧侧边栏 -->
        <div class="sidebar">
          <!-- 相关资讯 -->
          <div class="sidebar-card" v-if="relatedNews.length > 0">
            <h3 class="sidebar-title">
              <el-icon color="#ab47bc"><Connection /></el-icon>
              相关资讯
            </h3>
            <div class="related-list">
              <div
                v-for="item in relatedNews"
                :key="item.id"
                class="related-item"
                @click="goDetail(item.id)"
              >
                <div class="related-cover">
                  <el-image :src="item.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
                </div>
                <div class="related-info">
                  <h4 class="related-title">{{ item.title }}</h4>
                  <span class="related-date">{{ formatDate(item.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 热门资讯 -->
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
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Clock, View, Star, InfoFilled, HotWater, Connection
} from '@element-plus/icons-vue'
import { getNewsDetail, getHotNews, getRelatedNews } from '@/api/news'
import { addFavorite, deleteFavorite, checkFavorite } from '@/api/favorite'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 状态
const loading = ref(true)
const newsDetail = ref(null)
const hotNews = ref([])
const relatedNews = ref([])
const prevNews = ref(null)
const nextNews = ref(null)
const isFavorited = ref(false)

// 默认占位图
const defaultCover = 'https://picsum.photos/seed/news-cover/200/140'

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return typeof dateStr === 'string' ? dateStr.substring(0, 10) : ''
}

// 加载详情
const loadDetail = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const res = await getNewsDetail(id)
    newsDetail.value = res.data
  } catch (e) {
    console.error('加载资讯详情失败:', e)
  } finally {
    loading.value = false
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

// 加载相关资讯
const loadRelatedNews = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await getRelatedNews(id)
    relatedNews.value = res.data || []
  } catch (e) {
    console.error('加载相关资讯失败:', e)
  }
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/news/${id}`)
}

// 收藏/取消收藏
const handleFavorite = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再收藏')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  try {
    if (isFavorited.value) {
      await deleteFavorite('news', route.params.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite('news', route.params.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    console.error('收藏操作失败:', e)
  }
}

// 检查收藏状态
const checkFavStatus = async () => {
  if (!userStore.isLogin) return
  try {
    const res = await checkFavorite('news', route.params.id)
    isFavorited.value = res.data?.favorited || false
  } catch (e) {
    console.error('检查收藏状态失败:', e)
  }
}

// 监听路由变化（同一组件内切换文章）
watch(() => route.params.id, () => {
  loadDetail()
  loadRelatedNews()
  checkFavStatus()
  window.scrollTo({ top: 0 })
})

onMounted(() => {
  loadDetail()
  loadHotNews()
  loadRelatedNews()
  checkFavStatus()
})
</script>

<style lang="scss" scoped>
.news-detail-page {
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

/* 文章卡片 */
.article-card {
  background: #fff;
  border-radius: 12px;
  padding: 36px 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.article-header {
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 1px solid #eceff1;
}

.article-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a2a4a;
  line-height: 1.5;
  margin: 0 0 16px;
}

.article-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #90a4ae;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-tag {
  background: #e3f2fd;
  color: #1976d2;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.favorite-btn {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 16px;
  border: 1px solid #e0e6ed;
  cursor: pointer;
  transition: all 0.2s;
  color: #78909c;

  &:hover {
    border-color: #ffa726;
    color: #ffa726;
  }

  &.active {
    background: #fff3e0;
    border-color: #ffa726;
    color: #ffa726;
  }
}

/* 封面图 */
.article-cover {
  margin-bottom: 28px;
  border-radius: 8px;
  overflow: hidden;

  .cover-img {
    width: 100%;
    max-height: 400px;
  }
}

/* 正文富文本样式 */
.article-content {
  font-size: 15px;
  line-height: 1.9;
  color: #37474f;
  word-break: break-word;

  :deep(h1), :deep(h2), :deep(h3), :deep(h4) {
    color: #1a2a4a;
    margin: 24px 0 12px;
    font-weight: 600;
  }

  :deep(h1) { font-size: 22px; }
  :deep(h2) { font-size: 20px; }
  :deep(h3) { font-size: 18px; }

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

  :deep(li) {
    margin-bottom: 8px;
  }

  :deep(blockquote) {
    border-left: 4px solid #2c3e6b;
    background: #f0f4fa;
    padding: 12px 20px;
    margin: 16px 0;
    color: #455a64;
    border-radius: 0 8px 8px 0;
  }

  :deep(a) {
    color: #1976d2;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  :deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 16px 0;

    th, td {
      border: 1px solid #e0e6ed;
      padding: 10px 14px;
      text-align: left;
    }

    th {
      background: #f0f4fa;
      font-weight: 600;
      color: #1a2a4a;
    }
  }
}

/* 文章底部 */
.article-footer {
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid #eceff1;
}

.footer-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #b0bec5;
}

/* 上一篇/下一篇 */
.prev-next-nav {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.nav-item {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  cursor: pointer;
  border: 1px solid #eceff1;
  transition: all 0.3s;

  &:hover {
    border-color: #2c3e6b;
    box-shadow: 0 4px 12px rgba(44, 62, 107, 0.1);

    .nav-title {
      color: #2c3e6b;
    }
  }

  &.next {
    text-align: right;
  }
}

.nav-label {
  display: block;
  font-size: 12px;
  color: #90a4ae;
  margin-bottom: 6px;
}

.nav-title {
  font-size: 14px;
  color: #455a64;
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

/* 骨架屏 */
.detail-skeleton {
  background: #fff;
  border-radius: 12px;
  padding: 36px 40px;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 80px 0;
}

/* 侧边栏 */
.sidebar {
  width: 300px;
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

/* 相关资讯 */
.related-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.related-item {
  display: flex;
  gap: 12px;
  cursor: pointer;

  &:hover .related-title {
    color: #2c3e6b;
  }
}

.related-cover {
  width: 100px;
  height: 70px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
  }
}

.related-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.related-title {
  font-size: 13px;
  color: #455a64;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

.related-date {
  font-size: 11px;
  color: #b0bec5;
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
    min-width: 300px;
  }

  .article-card {
    padding: 24px 20px;
  }

  .article-title {
    font-size: 22px;
  }
}

@media (max-width: 576px) {
  .sidebar {
    flex-direction: column;
  }

  .prev-next-nav {
    flex-direction: column;
  }
}
</style>
