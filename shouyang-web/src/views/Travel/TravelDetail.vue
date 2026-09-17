<template>
  <div class="travel-detail-page">
    <div class="page-container">
      <!-- 面包屑导航 -->
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: `/travel/${type}` }">{{ config.name }}</el-breadcrumb-item>
          <el-breadcrumb-item>详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="detail-skeleton">
        <el-skeleton :rows="15" animated />
      </div>

      <template v-else-if="detail">
        <!-- 主内容区 -->
        <div class="detail-content">
          <!-- 封面大图 -->
          <div class="detail-cover" v-if="detail.coverImage">
            <el-image :src="detail.coverImage" fit="cover" class="cover-img" />
            <div class="cover-overlay">
              <h1 class="cover-title">{{ titleText }}</h1>
              <div class="cover-meta">
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ detail.viewCount || 0 }} 浏览
                </span>
                <span class="favorite-btn" :class="{ active: isFavorited }" @click="handleFavorite">
                  <el-icon><Star /></el-icon>
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </span>
              </div>
            </div>
          </div>

          <!-- 无封面时的标题区 -->
          <div v-else class="detail-header-plain">
            <h1 class="detail-title">{{ titleText }}</h1>
            <div class="detail-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ detail.viewCount || 0 }} 浏览
              </span>
              <span class="favorite-btn" :class="{ active: isFavorited }" @click="handleFavorite">
                <el-icon><Star /></el-icon>
                {{ isFavorited ? '已收藏' : '收藏' }}
              </span>
            </div>
          </div>

          <!-- 基本信息区 -->
          <div class="info-card" v-if="infoItems.length > 0">
            <h3 class="info-title">
              <el-icon color="#2c3e6b"><InfoFilled /></el-icon>
              基本信息
            </h3>
            <el-descriptions :column="2" border size="default">
              <el-descriptions-item
                v-for="item in infoItems"
                :key="item.label"
                :label="item.label"
              >
                {{ item.value || '暂无' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 详细介绍 -->
          <div class="content-card">
            <h3 class="content-title">
              <el-icon color="#2c3e6b"><Document /></el-icon>
              详细介绍
            </h3>
            <div class="article-content" v-html="detail.content || '<p>暂无详细介绍</p>'"></div>
          </div>
        </div>

        <!-- 侧边栏 -->
        <div class="detail-sidebar">
          <!-- 相关推荐 -->
          <div class="sidebar-card" v-if="relatedList.length > 0">
            <h3 class="sidebar-title">
              <el-icon color="#ab47bc"><Connection /></el-icon>
              相关推荐
            </h3>
            <div class="related-list">
              <div
                v-for="item in relatedList"
                :key="item.id"
                class="related-item"
                @click="goDetail(item.id)"
              >
                <div class="related-cover">
                  <el-image :src="item.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
                </div>
                <div class="related-info">
                  <h4 class="related-title">{{ item.name || item.title }}</h4>
                  <span class="related-extra">{{ relatedExtra(item) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 返回列表 -->
          <div class="sidebar-card">
            <el-button type="primary" plain @click="$router.back()" class="back-btn">
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
          </div>
        </div>
      </template>

      <!-- 不存在 -->
      <el-empty v-else description="内容不存在或已下架" class="empty-state" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  View, Star, InfoFilled, Document, Connection, ArrowLeft,
  Location, Phone, Clock, Money, User, Calendar, Guide
} from '@element-plus/icons-vue'
import { typeConfig, getTravelDetail, getTravelList } from '@/api/travel'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (val) => ['scenic', 'food', 'hotel', 'guide'].includes(val)
  }
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 配置
const config = computed(() => typeConfig[props.type])

// 状态
const loading = ref(true)
const detail = ref(null)
const relatedList = ref([])
const isFavorited = ref(false)

// 默认占位图
const defaultCover = 'https://picsum.photos/seed/travel-cover/200/140'

// 标题（景点/美食/酒店用 name，攻略用 title）
const titleText = computed(() => {
  return detail.value?.name || detail.value?.title || '详情'
})

// 基本信息项（根据 type 动态生成）
const infoItems = computed(() => {
  if (!detail.value) return []
  const d = detail.value
  const items = []

  if (props.type === 'scenic') {
    items.push({ label: '景点等级', value: d.level })
    items.push({ label: '门票价格', value: d.ticketPrice })
    items.push({ label: '开放时间', value: d.openTime })
    items.push({ label: '联系电话', value: d.phone })
    items.push({ label: '详细地址', value: d.address })
  } else if (props.type === 'food') {
    items.push({ label: '美食类型', value: d.category })
    items.push({ label: '简要介绍', value: d.description })
  } else if (props.type === 'hotel') {
    items.push({ label: '酒店星级', value: d.star ? d.star + '星' : '' })
    items.push({ label: '价格区间', value: d.priceRange })
    items.push({ label: '联系电话', value: d.phone })
    items.push({ label: '详细地址', value: d.address })
  } else if (props.type === 'guide') {
    items.push({ label: '作者/来源', value: d.author })
    items.push({ label: '游玩天数', value: d.days })
    items.push({ label: '路线概要', value: d.route })
    items.push({ label: '发布时间', value: formatDate(d.createTime) })
  }

  return items.filter(item => item.value)
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return typeof dateStr === 'string' ? dateStr.substring(0, 10) : ''
}

// 相关推荐额外信息
const relatedExtra = (item) => {
  if (props.type === 'scenic') return item.level || ''
  if (props.type === 'food') return item.category || ''
  if (props.type === 'hotel') return (item.star ? item.star + '星' : '') + (item.priceRange ? ' · ' + item.priceRange : '')
  if (props.type === 'guide') return item.days || ''
  return ''
}

// 加载详情
const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getTravelDetail(props.type, id)
    detail.value = res.data
  } catch (e) {
    console.error('加载详情失败:', e)
  } finally {
    loading.value = false
  }
}

// 加载相关推荐（同类型最新的其他内容）
const loadRelated = async () => {
  try {
    const res = await getTravelList(props.type, { page: 1, size: 6 })
    const list = res.data?.records || []
    // 排除当前内容
    relatedList.value = list.filter(item => item.id !== Number(route.params.id)).slice(0, 5)
  } catch (e) {
    console.error('加载相关推荐失败:', e)
  }
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/travel/${props.type}/${id}`)
}

// 收藏/取消收藏
const handleFavorite = () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再收藏')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  isFavorited.value = !isFavorited.value
  ElMessage.success(isFavorited.value ? '收藏成功' : '已取消收藏')
}

// 监听路由 id 变化（同一类型内切换文章）
watch(() => route.params.id, () => {
  loadDetail()
  loadRelated()
  window.scrollTo({ top: 0 })
})

// 监听 type 变化（从景点详情切换到美食详情等跨类型导航）
watch(() => props.type, () => {
  isFavorited.value = false
  loadDetail()
  loadRelated()
  window.scrollTo({ top: 0 })
})

onMounted(() => {
  loadDetail()
  loadRelated()
})
</script>

<style lang="scss" scoped>
.travel-detail-page {
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
.detail-content {
  flex: 1;
  min-width: 0;
}

.page-container > div:not(.breadcrumb-bar):not(.detail-skeleton):not(.empty-state) {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

/* 封面大图 */
.detail-cover {
  position: relative;
  width: 100%;
  height: 360px;
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
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent 60%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 30px;
}

.cover-title {
  font-size: 30px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.cover-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.favorite-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.2s;
  backdrop-filter: blur(4px);

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }

  &.active {
    background: #ffa726;
    color: #fff;
  }
}

/* 无封面时的标题区 */
.detail-header-plain {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
}

.detail-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a2a4a;
  margin: 0 0 16px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #90a4ae;
  font-size: 14px;

  .favorite-btn {
    background: #f5f7fa;
    color: #78909c;

    &.active {
      background: #fff3e0;
      color: #ffa726;
    }
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

/* 富文本正文 */
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
}

/* 侧边栏 */
.detail-sidebar {
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

/* 相关推荐 */
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

.related-extra {
  font-size: 11px;
  color: #b0bec5;
}

/* 返回按钮 */
.back-btn {
  width: 100%;
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
  .page-container > div:not(.breadcrumb-bar):not(.detail-skeleton):not(.empty-state) {
    flex-direction: column;
  }

  .detail-sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .sidebar-card {
    flex: 1;
    min-width: 300px;
  }

  .detail-cover {
    height: 260px;
  }

  .cover-title {
    font-size: 24px;
  }
}

@media (max-width: 576px) {
  .detail-sidebar {
    flex-direction: column;
  }

  .detail-cover {
    height: 200px;
  }

  .cover-overlay {
    padding: 20px;
  }

  .cover-title {
    font-size: 20px;
  }
}
</style>
