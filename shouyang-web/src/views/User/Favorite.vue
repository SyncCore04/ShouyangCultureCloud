<template>
  <div class="favorite-page">
    <div class="content-card">
      <h3 class="card-title">我的收藏</h3>

      <!-- Tab 切换 -->
      <el-tabs v-model="activeType" @tab-change="handleTabChange" class="favorite-tabs">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="资讯" name="news" />
        <el-tab-pane label="景点" name="scenic" />
        <el-tab-pane label="美食" name="food" />
        <el-tab-pane label="酒店" name="hotel" />
        <el-tab-pane label="活动" name="activity" />
        <el-tab-pane label="文创" name="product" />
      </el-tabs>

      <!-- 收藏列表 -->
      <div v-if="loading" class="favorite-grid">
        <el-skeleton v-for="i in 4" :key="i" :rows="3" animated class="skeleton-card" />
      </div>

      <div v-else-if="list.length > 0" class="favorite-grid">
        <div v-for="item in list" :key="item.id" class="favorite-item" @click="goDetail(item)">
          <el-image :src="item.coverImage || defaultCover" fit="cover" class="item-cover" :lazy="true" />
          <div class="item-info">
            <h4 class="item-title">{{ item.title || '无标题' }}</h4>
            <p class="item-desc">{{ item.description || '暂无描述' }}</p>
            <div class="item-footer">
              <el-tag size="small" :type="getTypeTagType(item.targetType)">{{ getTypeLabel(item.targetType) }}</el-tag>
              <span class="item-time">{{ formatTime(item.createTime) }}</span>
            </div>
          </div>
          <el-button
            type="danger"
            size="small"
            plain
            class="cancel-btn"
            @click.stop="handleCancel(item)"
          >
            <el-icon><Delete /></el-icon>
            取消收藏
          </el-button>
        </div>
      </div>

      <el-empty v-else description="暂无收藏内容" class="empty-state">
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </el-empty>

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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getFavoriteList, deleteFavorite, favoriteTypeConfig } from '@/api/favorite'

const router = useRouter()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)
const activeType = ref('')
const defaultCover = 'https://picsum.photos/seed/favorite-cover/300/200'

const loadList = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (activeType.value) params.targetType = activeType.value
    const res = await getFavoriteList(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载收藏列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  loadList()
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
}

const handleCancel = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteFavorite(item.targetType, item.targetId)
    ElMessage.success('已取消收藏')
    loadList()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('取消收藏失败:', e)
    }
  }
}

const goDetail = (item) => {
  const config = favoriteTypeConfig[item.targetType]
  if (!config) return
  if (item.targetType === 'news') {
    router.push(`/news/detail/${item.targetId}`)
  } else if (item.targetType === 'activity') {
    router.push(`/service/activity/${item.targetId}`)
  } else {
    router.push(`${config.route}/${item.targetId}`)
  }
}

const getTypeLabel = (type) => favoriteTypeConfig[type]?.label || type

const getTypeTagType = (type) => {
  const map = { news: '', scenic: 'success', food: 'warning', hotel: 'info', activity: 'danger', product: '' }
  return map[type] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  return time.substring(0, 10)
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.favorite-page { min-height: 100%; }
.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.card-title { font-size: 18px; font-weight: 600; color: #1a2a4a; margin: 0 0 20px; padding-bottom: 16px; border-bottom: 1px solid #f0f2f5; }

.favorite-tabs { margin-bottom: 20px;
  :deep(.el-tabs__item) { font-size: 14px; }
  :deep(.el-tabs__active-bar) { background-color: #2c3e6b; }
  :deep(.el-tabs__item.is-active) { color: #2c3e6b; font-weight: 600; }
}

.favorite-grid { display: flex; flex-direction: column; gap: 16px; margin-bottom: 24px; }
.skeleton-card { padding: 16px; border: 1px solid #f0f2f5; border-radius: 10px; }

.favorite-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f0f2f5;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  &:hover {
    border-color: #c5d3e8;
    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
    .item-title { color: #2c3e6b; }
  }
}
.item-cover { width: 140px; height: 100px; border-radius: 8px; flex-shrink: 0; background: #f0f2f5; }
.item-info { flex: 1; min-width: 0; }
.item-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 6px;
  transition: color 0.2s;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-desc {
  font-size: 13px;
  color: #78909c;
  margin: 0 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.item-footer { display: flex; align-items: center; gap: 12px; }
.item-time { font-size: 12px; color: #b0bec5; }
.cancel-btn { flex-shrink: 0; }

.empty-state { padding: 60px 0; }
</style>
