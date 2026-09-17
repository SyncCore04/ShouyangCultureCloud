<template>
  <div class="dashboard-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6" v-for="card in statCards" :key="card.key">
        <div class="stat-card" :style="{ background: card.gradient }">
          <div class="stat-icon">
            <el-icon :size="36"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
          <div v-if="card.today !== undefined" class="stat-today">
            今日 +{{ card.today }}
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表和热门资讯 -->
    <el-row :gutter="16" class="chart-row">
      <!-- 资讯发布趋势 -->
      <el-col :span="16">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">资讯发布趋势（最近 7 天）</span>
            </div>
          </template>
          <div ref="chartRef" class="trend-chart"></div>
        </el-card>
      </el-col>

      <!-- 热门资讯 -->
      <el-col :span="8">
        <el-card class="hot-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">热门资讯 TOP5</span>
            </div>
          </template>
          <div class="hot-list" v-loading="loading">
            <div
              v-for="(item, index) in hotNewsList"
              :key="item.id"
              class="hot-item"
            >
              <div class="hot-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="hot-content">
                <div class="hot-title" @click="viewNews(item.id)">{{ item.title }}</div>
                <div class="hot-meta">
                  <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
                </div>
              </div>
            </div>
            <el-empty v-if="hotNewsList.length === 0" description="暂无数据" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { User, Document, Calendar, Tickets, View } from '@element-plus/icons-vue'
import { getDashboardStats, getDashboardTrend, getHotNews } from '@/api/dashboard'

const router = useRouter()
const chartRef = ref(null)
let chartInstance = null
const loading = ref(false)

const stats = reactive({
  userTotal: 0,
  newsTotal: 0,
  activityTotal: 0,
  registerTotal: 0,
  todayNewUsers: 0,
  todayNews: 0
})

const hotNewsList = ref([])

// 统计卡片配置
const statCards = ref([
  {
    key: 'user',
    label: '用户总数',
    value: 0,
    today: 0,
    icon: 'User',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    key: 'news',
    label: '资讯总数',
    value: 0,
    today: 0,
    icon: 'Document',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    key: 'activity',
    label: '活动总数',
    value: 0,
    icon: 'Calendar',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    key: 'register',
    label: '报名总数',
    value: 0,
    icon: 'Tickets',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    const data = res.data
    stats.userTotal = data.userTotal || 0
    stats.newsTotal = data.newsTotal || 0
    stats.activityTotal = data.activityTotal || 0
    stats.registerTotal = data.registerTotal || 0
    stats.todayNewUsers = data.todayNewUsers || 0
    stats.todayNews = data.todayNews || 0

    // 更新卡片数据
    statCards.value[0].value = stats.userTotal
    statCards.value[0].today = stats.todayNewUsers
    statCards.value[1].value = stats.newsTotal
    statCards.value[1].today = stats.todayNews
    statCards.value[2].value = stats.activityTotal
    statCards.value[3].value = stats.registerTotal
  } catch (e) {
    console.error('加载统计数据失败:', e)
  }
}

// 加载趋势图
const loadTrend = async () => {
  try {
    const res = await getDashboardTrend(7)
    const data = res.data
    await nextTick()
    initChart(data.dates, data.counts)
  } catch (e) {
    console.error('加载趋势数据失败:', e)
  }
}

// 加载热门资讯
const loadHotNews = async () => {
  loading.value = true
  try {
    const res = await getHotNews(5)
    hotNewsList.value = res.data || []
  } catch (e) {
    console.error('加载热门资讯失败:', e)
  } finally {
    loading.value = false
  }
}

// 初始化 ECharts
const initChart = (dates, counts) => {
  if (!chartRef.value) return
  if (chartInstance) {
    chartInstance.dispose()
  }
  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>发布数量：{c} 篇'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#909399' }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f0f2f5' } },
      axisLabel: { color: '#909399' }
    },
    series: [
      {
        name: '发布数量',
        type: 'line',
        smooth: true,
        data: counts,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: '#2c3e6b'
        },
        itemStyle: {
          color: '#2c3e6b',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(44, 62, 107, 0.3)' },
            { offset: 1, color: 'rgba(44, 62, 107, 0.02)' }
          ])
        }
      }
    ]
  }
  chartInstance.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 查看资讯详情
const viewNews = (id) => {
  router.push(`/news`)
}

onMounted(() => {
  loadStats()
  loadTrend()
  loadHotNews()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  padding: 16px;
}
.stats-row {
  margin-bottom: 16px;
}
.stat-card {
  position: relative;
  border-radius: 12px;
  padding: 24px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
  overflow: hidden;
  min-height: 110px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  }
}
.stat-icon {
  opacity: 0.9;
  flex-shrink: 0;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  line-height: 1.2;
}
.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 4px;
}
.stat-today {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
}
.chart-row {
  .chart-card,
  .hot-card {
    height: 100%;
    :deep(.el-card__body) {
      padding: 16px;
    }
  }
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.trend-chart {
  width: 100%;
  height: 320px;
}
.hot-list {
  min-height: 320px;
}
.hot-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
  &:last-child {
    border-bottom: none;
  }
}
.hot-rank {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  flex-shrink: 0;
  background: #c0c4cc;
  &.rank-1 { background: #f56c6c; }
  &.rank-2 { background: #e6a23c; }
  &.rank-3 { background: #67c23a; }
}
.hot-content {
  flex: 1;
  min-width: 0;
}
.hot-title {
  font-size: 14px;
  color: #303133;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  &:hover {
    color: #2c3e6b;
  }
}
.hot-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
  .el-icon {
    font-size: 12px;
  }
}
</style>
