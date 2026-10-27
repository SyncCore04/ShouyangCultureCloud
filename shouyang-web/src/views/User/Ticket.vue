<template>
  <div class="ticket-page">
    <div class="content-card">
      <h3 class="card-title">我的门票</h3>

      <div v-if="loading" class="order-list">
        <el-skeleton v-for="i in 3" :key="i" :rows="3" animated class="skeleton-item" />
      </div>

      <div v-else-if="list.length > 0" class="order-list">
        <div v-for="item in list" :key="item.id" class="order-item">
          <el-image :src="item.coverImage || defaultCover" fit="cover" class="item-cover" :lazy="true" @click="goDetail(item)" />
          <div class="item-info" @click="goDetail(item)">
            <h4 class="item-title">{{ item.ticketName || '票务名称' }}</h4>
            <div class="item-meta">
              <span class="meta-item"><el-icon><Clock /></el-icon>{{ formatTime(item.startTime) }} ~ {{ formatTime(item.endTime) }}</span>
              <span class="meta-item"><el-icon><Location /></el-icon>{{ item.venue || '待定' }}</span>
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ item.name }}（{{ item.phone }}）· {{ item.quantity }} 张 · {{ formatPrice(item.price) }}
              </span>
            </div>
            <div class="item-footer">
              <el-tag size="small" :type="getTicketStatus(item).type">{{ getTicketStatus(item).label }}</el-tag>
              <span class="order-time">预订时间：{{ formatTime(item.createTime) }}</span>
            </div>
          </div>
          <div class="item-actions">
            <el-button
              v-if="canCancel(item)"
              type="danger"
              size="small"
              plain
              @click="handleCancel(item)"
            >
              取消预订
            </el-button>
            <el-button v-else type="info" size="small" disabled>
              {{ getTicketStatus(item).label === '已结束' ? '已结束' : '已开始' }}
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无门票订单" class="empty-state">
        <el-button type="primary" @click="$router.push('/service/ticket')">去预订</el-button>
      </el-empty>

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
import { Clock, Location, User } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getMyTicketList, cancelTicketOrder } from '@/api/service'

const router = useRouter()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)
const defaultCover = 'https://picsum.photos/seed/ticket-cover/300/200'

const loadList = async () => {
  loading.value = true
  try {
    const res = await getMyTicketList({ page: currentPage.value, size: pageSize.value })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载门票订单失败:', e)
  } finally {
    loading.value = false
  }
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
}

// 是否可取消（票务未结束）
const canCancel = (item) => {
  if (!item.endTime) return true
  return Date.now() < new Date(item.endTime).getTime()
}

const handleCancel = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消「${item.ticketName}」的 ${item.quantity} 张门票吗？取消后库存将释放给其他用户。`,
      '确认取消',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    )
    await cancelTicketOrder(item.id)
    ElMessage.success('已取消预订，库存已回补')
    loadList()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('取消预订失败:', e)
    }
  }
}

const goDetail = (item) => {
  router.push(`/service/ticket/${item.ticketId}`)
}

// 票务状态（根据时间判断）
const getTicketStatus = (item) => {
  const now = Date.now()
  const start = item.startTime ? new Date(item.startTime).getTime() : 0
  const end = item.endTime ? new Date(item.endTime).getTime() : 0
  if (end && now > end) return { label: '已结束', type: 'info' }
  if (start && now >= start && now <= end) return { label: '进行中', type: 'success' }
  return { label: '未开始', type: 'primary' }
}

const formatPrice = (price) => {
  if (price === null || price === undefined) return '价格待定'
  return Number(price) === 0 ? '免费' : `¥${price}/张`
}

const formatTime = (time) => {
  if (!time) return '待定'
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.ticket-page { min-height: 100%; }
.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.card-title { font-size: 18px; font-weight: 600; color: #1a2a4a; margin: 0 0 20px; padding-bottom: 16px; border-bottom: 1px solid #f0f2f5; }

.order-list { display: flex; flex-direction: column; gap: 16px; margin-bottom: 24px; }
.skeleton-item { padding: 16px; border: 1px solid #f0f2f5; border-radius: 10px; }

.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f0f2f5;
  border-radius: 10px;
  transition: all 0.2s;
  &:hover {
    border-color: #c5d3e8;
    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  }
}
.item-cover { width: 160px; height: 110px; border-radius: 8px; flex-shrink: 0; background: #f0f2f5; cursor: pointer; }
.item-info { flex: 1; min-width: 0; cursor: pointer; }
.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-meta { display: flex; flex-direction: column; gap: 6px; margin-bottom: 10px; }
.meta-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #78909c; }
.item-footer { display: flex; align-items: center; gap: 12px; }
.order-time { font-size: 12px; color: #b0bec5; }
.item-actions { flex-shrink: 0; }
.empty-state { padding: 60px 0; }
</style>
