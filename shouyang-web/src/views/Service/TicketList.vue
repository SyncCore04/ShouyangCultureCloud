<template>
  <div class="ticket-list-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">票务预订</h1>
        <p class="page-subtitle">在线查看各类演出、展览票务信息</p>
      </div>

      <div v-if="loading" class="ticket-grid">
        <el-skeleton v-for="i in 6" :key="i" :rows="4" animated class="skeleton-card" />
      </div>

      <div v-else-if="ticketList.length > 0" class="ticket-grid">
        <div v-for="ticket in ticketList" :key="ticket.id" class="ticket-card">
          <div class="card-cover">
            <el-image :src="ticket.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
            <span class="price-tag" :class="{ free: ticket.price === 0 }">
              {{ ticket.price === 0 ? '免费' : '¥' + ticket.price }}
            </span>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ ticket.name }}</h4>
            <div class="card-info">
              <span class="info-item"><el-icon><Location /></el-icon>{{ ticket.venue || '待定' }}</span>
              <span class="info-item"><el-icon><Clock /></el-icon>{{ formatDate(ticket.startTime) }}</span>
            </div>
            <div class="card-footer">
              <span class="remain" :class="{ low: ticket.remainCount <= 10 }">
                剩余 {{ ticket.remainCount }} 张
              </span>
              <el-button type="primary" size="small" plain disabled>暂不支持购买</el-button>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无票务信息" class="empty-state" />

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
import { Location, Clock } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getTicketList } from '@/api/service'

const loading = ref(true)
const ticketList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(9)
const defaultCover = 'https://picsum.photos/seed/ticket-cover/400/240'

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getTicketList({ page: currentPage.value, size: pageSize.value })
    ticketList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载票务列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.ticket-list-page {
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

.ticket-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}
.skeleton-card { background: #fff; border-radius: 12px; padding: 16px; height: 320px; }

.ticket-card {
  background: #fff; border-radius: 12px; overflow: hidden;
  border: 1px solid #eceff1; transition: all 0.3s;
  &:hover { transform: translateY(-4px); box-shadow: 0 10px 24px rgba(0,0,0,0.1); }
}
.card-cover { position: relative; width: 100%; height: 180px; overflow: hidden; background: #e0e6ed; }
.cover-img { width: 100%; height: 100%; }
.price-tag {
  position: absolute; top: 12px; right: 12px;
  padding: 4px 12px; border-radius: 12px;
  background: rgba(245, 108, 108, 0.95); color: #fff;
  font-size: 14px; font-weight: 700;
  &.free { background: rgba(103, 194, 58, 0.95); }
}
.card-body { padding: 16px 18px; }
.card-title { font-size: 16px; font-weight: 600; color: #1a2a4a; margin: 0 0 10px;
  display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.card-info { display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
.info-item { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #90a4ae; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.remain { font-size: 12px; color: #78909c; &.low { color: #e6a23c; } }
.empty-state { background: #fff; border-radius: 10px; padding: 60px 0; margin-bottom: 24px; }

@media (max-width: 992px) { .ticket-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 576px) { .ticket-grid { grid-template-columns: 1fr; } }
</style>
