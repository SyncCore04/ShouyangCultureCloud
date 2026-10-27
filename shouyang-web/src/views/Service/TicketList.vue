<template>
  <div class="ticket-list-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">票务预订</h1>
        <p class="page-subtitle">在线查看各类演出、展览票务信息，支持在线预订</p>
      </div>

      <!-- 可视化看板 -->
      <div v-if="showDashboard" class="dashboard">
        <div class="stat-cards">
          <div class="stat-card" v-for="stat in statCards" :key="stat.label">
            <div class="stat-icon" :style="{ background: stat.bg }">
              <el-icon :size="22" :color="stat.color"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </div>
        <div class="chart-row">
          <div class="chart-card">
            <h4 class="chart-title">票价区间分布</h4>
            <div ref="priceChartRef" class="chart-box"></div>
          </div>
          <div class="chart-card">
            <h4 class="chart-title">余票率排行 TOP5</h4>
            <div ref="rankChartRef" class="chart-box"></div>
          </div>
        </div>
      </div>

      <!-- 票务列表 -->
      <div v-if="loading" class="ticket-grid">
        <el-skeleton v-for="i in 6" :key="i" :rows="4" animated class="skeleton-card" />
      </div>

      <div v-else-if="ticketList.length > 0" class="ticket-grid">
        <div v-for="ticket in ticketList" :key="ticket.id" class="ticket-card" @click="goDetail(ticket)">
          <div class="card-cover">
            <el-image :src="ticket.coverImage || defaultCover" fit="cover" class="cover-img" :lazy="true" />
            <span class="price-tag" :class="{ free: ticket.price === 0 }">
              {{ ticket.price === 0 ? '免费' : '¥' + ticket.price }}
            </span>
            <!-- 状态标签 -->
            <span
              v-if="getStatus(ticket).label"
              class="status-tag"
              :class="'status-' + getStatus(ticket).type"
            >
              {{ getStatus(ticket).label }}
            </span>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ ticket.name }}</h4>
            <div class="card-info">
              <span class="info-item"><el-icon><Location /></el-icon>{{ ticket.venue || '待定' }}</span>
              <span class="info-item"><el-icon><Clock /></el-icon>{{ formatDate(ticket.startTime) }}</span>
            </div>
            <!-- 库存进度条 -->
            <div class="stock-progress">
              <el-progress
                :percentage="remainPercent(ticket)"
                :stroke-width="6"
                :color="progressColor(ticket)"
                :show-text="false"
              />
              <div class="stock-text">
                <span class="remain" :class="{ low: isLowStock(ticket) }">
                  剩余 {{ ticket.remainCount }} 张
                </span>
                <span class="percent">{{ remainPercent(ticket) }}%</span>
              </div>
            </div>
            <div class="card-footer">
              <span class="hint">点击查看详情</span>
              <el-button
                type="primary"
                size="small"
                :disabled="isSoldOut(ticket) || isEnded(ticket)"
                @click.stop="goDetail(ticket)"
              >
                {{ isSoldOut(ticket) ? '已售罄' : (isEnded(ticket) ? '已结束' : '立即预订') }}
              </el-button>
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
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Clock, Ticket, DataLine, Wallet, Present } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import Pagination from '@/components/Pagination.vue'
import { getTicketList, getTicketStats } from '@/api/service'

const router = useRouter()
const loading = ref(true)
const ticketList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(9)
const defaultCover = 'https://picsum.photos/seed/ticket-cover/400/240'

// 看板
const showDashboard = ref(false)
const stats = ref(null)
const priceChartRef = ref(null)
const rankChartRef = ref(null)
let priceChart = null
let rankChart = null

// 统计卡片
const statCards = computed(() => {
  if (!stats.value) return []
  return [
    { label: '在售票种', value: stats.value.totalTypes ?? 0, icon: Ticket, color: '#409eff', bg: '#ecf5ff' },
    { label: '总余票（张）', value: stats.value.totalRemain ?? 0, icon: DataLine, color: '#67c23a', bg: '#f0f9eb' },
    { label: '平均票价（元）', value: stats.value.avgPrice ?? 0, icon: Wallet, color: '#e6a23c', bg: '#fdf6ec' },
    { label: '免费票种', value: stats.value.freeCount ?? 0, icon: Present, color: '#f56c6c', bg: '#fef0f0' }
  ]
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// ========== 卡片状态与库存 ==========
const isSoldOut = (ticket) => (ticket.remainCount || 0) <= 0
const isLowStock = (ticket) => !isSoldOut(ticket) && (ticket.remainCount || 0) <= 10

const isEnded = (ticket) => {
  if (!ticket.endTime) return false
  return Date.now() > new Date(ticket.endTime).getTime()
}

const remainPercent = (ticket) => {
  if (!ticket.totalCount) return 0
  const remain = ticket.remainCount || 0
  return Math.min(100, Math.max(0, Math.round(remain / ticket.totalCount * 100)))
}

const progressColor = (ticket) => {
  if (isSoldOut(ticket)) return '#c0c4cc'
  if (isLowStock(ticket)) return '#e6a23c'
  return '#67c23a'
}

// 票务状态标签
const getStatus = (ticket) => {
  const now = Date.now()
  const start = ticket.startTime ? new Date(ticket.startTime).getTime() : 0
  const end = ticket.endTime ? new Date(ticket.endTime).getTime() : 0
  if (end && now > end) return { label: '已结束', type: 'info' }
  if (start && now >= start && now <= end) return { label: '进行中', type: 'success' }
  if (start && now < start && start - now < 48 * 3600 * 1000) return { label: '即将开场', type: 'warning' }
  return { label: '', type: 'info' }
}

const goDetail = (ticket) => {
  router.push(`/service/ticket/${ticket.id}`)
}

// ========== 数据加载 ==========
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

const loadStats = async () => {
  try {
    const res = await getTicketStats()
    if (res.data && (res.data.totalTypes ?? 0) > 0) {
      stats.value = res.data
      showDashboard.value = true
      // 等待 DOM 渲染后初始化图表
      nextTick(() => {
        initPriceChart()
        initRankChart()
      })
    }
  } catch (e) {
    // 统计接口异常时隐藏看板，不阻塞列表
    console.error('加载票务统计失败:', e)
    showDashboard.value = false
  }
}

const initPriceChart = () => {
  if (!priceChartRef.value || !stats.value) return
  priceChart = echarts.init(priceChartRef.value)
  const ranges = stats.value.priceRanges || []
  priceChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 16, right: 16, top: 24, bottom: 24, containLabel: true },
    xAxis: {
      type: 'category',
      data: ranges.map(r => r.range),
      axisLabel: { color: '#78909c', fontSize: 12 },
      axisLine: { lineStyle: { color: '#e0e6ed' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { color: '#78909c', fontSize: 12 },
      splitLine: { lineStyle: { color: '#f0f2f5' } }
    },
    series: [{
      type: 'bar',
      barWidth: '45%',
      data: ranges.map(r => r.count),
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409eff' },
          { offset: 1, color: '#79bbff' }
        ])
      },
      label: { show: true, position: 'top', color: '#546e7a', fontSize: 12 }
    }]
  })
}

const initRankChart = () => {
  if (!rankChartRef.value || !stats.value) return
  rankChart = echarts.init(rankChartRef.value)
  const rank = (stats.value.remainRank || []).slice().reverse() // 倒序让第一名显示在顶部
  rankChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0]
        const item = rank[p.dataIndex]
        return `${item.name}<br/>余票率：${item.remainRate}%（剩 ${item.remainCount} 张）`
      }
    },
    grid: { left: 16, right: 40, top: 10, bottom: 16, containLabel: true },
    xAxis: {
      type: 'value',
      max: 100,
      axisLabel: { color: '#78909c', fontSize: 12, formatter: '{value}%' },
      splitLine: { lineStyle: { color: '#f0f2f5' } }
    },
    yAxis: {
      type: 'category',
      data: rank.map(r => r.name.length > 8 ? r.name.substring(0, 8) + '…' : r.name),
      axisLabel: { color: '#546e7a', fontSize: 12 },
      axisLine: { show: false },
      axisTick: { show: false }
    },
    series: [{
      type: 'bar',
      barWidth: '50%',
      data: rank.map(r => r.remainRate),
      itemStyle: {
        borderRadius: [0, 6, 6, 0],
        color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#95d475' }
        ])
      },
      label: { show: true, position: 'right', color: '#546e7a', fontSize: 12, formatter: '{c}%' }
    }]
  })
}

const handleChartResize = () => {
  priceChart?.resize()
  rankChart?.resize()
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  loadList()
  loadStats()
  window.addEventListener('resize', handleChartResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleChartResize)
  priceChart?.dispose()
  rankChart?.dispose()
  priceChart = null
  rankChart = null
})
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

/* ========== 可视化看板 ========== */
.dashboard {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #eceff1;
  padding: 20px;
  margin-bottom: 24px;
}
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px;
  background: #f8fafc;
  border-radius: 10px;
  transition: all 0.3s;
  &:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(0,0,0,0.06); }
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-value { font-size: 24px; font-weight: 700; color: #1a2a4a; line-height: 1.2; }
.stat-label { font-size: 13px; color: #90a4ae; margin-top: 2px; }

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.chart-card {
  border: 1px solid #f0f2f5;
  border-radius: 10px;
  padding: 16px;
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 8px;
}
.chart-box { height: 260px; }

/* ========== 票务卡片 ========== */
.ticket-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}
.skeleton-card { background: #fff; border-radius: 12px; padding: 16px; height: 340px; }

.ticket-card {
  background: #fff; border-radius: 12px; overflow: hidden;
  border: 1px solid #eceff1; transition: all 0.3s;
  cursor: pointer;
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
.status-tag {
  position: absolute; top: 12px; left: 12px;
  padding: 3px 10px; border-radius: 12px;
  font-size: 12px; font-weight: 600; color: #fff;
  &.status-success { background: rgba(103, 194, 58, 0.92); }
  &.status-warning { background: rgba(230, 162, 60, 0.92); }
  &.status-info { background: rgba(144, 147, 153, 0.92); }
  &.status-primary { background: rgba(64, 158, 255, 0.92); }
}
.card-body { padding: 16px 18px; }
.card-title { font-size: 16px; font-weight: 600; color: #1a2a4a; margin: 0 0 10px;
  display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.card-info { display: flex; flex-direction: column; gap: 6px; margin-bottom: 12px; }
.info-item { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #90a4ae; }

.stock-progress { margin-bottom: 12px; }
.stock-text {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
  .percent { font-size: 12px; color: #b0bec5; }
}
.remain { font-size: 12px; color: #78909c; &.low { color: #e6a23c; font-weight: 600; } }
.card-footer {
  display: flex; justify-content: space-between; align-items: center;
  .hint { font-size: 12px; color: #c0c4cc; }
}
.empty-state { background: #fff; border-radius: 10px; padding: 60px 0; margin-bottom: 24px; }

@media (max-width: 992px) {
  .ticket-grid { grid-template-columns: repeat(2, 1fr); }
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .chart-row { grid-template-columns: 1fr; }
}
@media (max-width: 576px) { .ticket-grid { grid-template-columns: 1fr; } }
</style>
