<template>
  <div class="venue-list-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">场馆预订</h1>
        <p class="page-subtitle">了解寿阳各类文化体育场馆信息</p>
      </div>

      <div v-if="loading" class="venue-grid">
        <el-skeleton v-for="i in 6" :key="i" :rows="4" animated class="skeleton-card" />
      </div>

      <div v-else-if="venueList.length > 0" class="venue-grid">
        <div v-for="venue in venueList" :key="venue.id" class="venue-card" @click="goDetail(venue.id)">
          <div class="card-cover">
            <el-image :src="venue.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
            <span class="status-tag open">开放中</span>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ venue.name }}</h4>
            <div class="card-info">
              <span class="info-item"><el-icon><Location /></el-icon>{{ venue.address || '暂无地址' }}</span>
              <span class="info-item"><el-icon><Clock /></el-icon>{{ venue.openTime || '暂无开放时间' }}</span>
            </div>
            <p class="card-desc">{{ venue.description || '暂无介绍' }}</p>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无场馆信息" class="empty-state" />

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
import { Location, Clock } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getVenueList } from '@/api/service'

const router = useRouter()
const loading = ref(true)
const venueList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(9)
const defaultCover = 'https://picsum.photos/seed/venue-cover/400/240'

const loadList = async () => {
  loading.value = true
  try {
    const res = await getVenueList({ page: currentPage.value, size: pageSize.value })
    venueList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载场馆列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goDetail = (id) => {
  router.push(`/service/venue/${id}`)
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.venue-list-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}
.page-header { margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; color: #1a2a4a; margin: 0 0 8px; }
.page-subtitle { font-size: 14px; color: #78909c; margin: 0; }

.venue-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}
.skeleton-card { background: #fff; border-radius: 12px; padding: 16px; height: 340px; }

.venue-card {
  background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer;
  border: 1px solid #eceff1; transition: all 0.3s;
  display: flex; flex-direction: column;
  &:hover { transform: translateY(-4px); box-shadow: 0 10px 24px rgba(0,0,0,0.1);
    .card-title { color: #2c3e6b; } }
}
.card-cover { position: relative; width: 100%; height: 180px; overflow: hidden; background: #e0e6ed; }
.cover-img { width: 100%; height: 100%; transition: transform 0.5s; }
.venue-card:hover .cover-img { transform: scale(1.05); }
.status-tag {
  position: absolute; top: 12px; right: 12px;
  padding: 4px 12px; border-radius: 12px;
  font-size: 12px; font-weight: 600; color: #fff;
  &.open { background: rgba(103, 194, 58, 0.95); }
}
.card-body { padding: 16px 18px; flex: 1; display: flex; flex-direction: column; }
.card-title { font-size: 16px; font-weight: 600; color: #1a2a4a; margin: 0 0 10px;
  display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; transition: color 0.2s; }
.card-info { display: flex; flex-direction: column; gap: 6px; margin-bottom: 10px; }
.info-item { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #90a4ae; }
.card-desc { font-size: 13px; color: #78909c; line-height: 1.6; margin: 0;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.empty-state { background: #fff; border-radius: 10px; padding: 60px 0; margin-bottom: 24px; }

@media (max-width: 992px) { .venue-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 576px) { .venue-grid { grid-template-columns: 1fr; } }
</style>
