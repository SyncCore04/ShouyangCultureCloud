<template>
  <div class="dashboard-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <div class="stat-card" :style="{ background: card.gradient }">
          <div class="stat-icon">
            <el-icon :size="32" color="#fff"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-title">{{ card.title }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">资讯发布趋势</h3>
            <el-radio-group v-model="chartRange" size="small" @change="loadChartData">
              <el-radio-button value="7">近7天</el-radio-button>
              <el-radio-button value="30">近30天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="chartRef" class="chart-container"></div>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">热门资讯 TOP5</h3>
          </div>
          <div class="hot-list">
            <div v-for="(item, index) in hotNews" :key="item.id" class="hot-item">
              <span class="hot-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
              <span class="hot-title text-ellipsis">{{ item.title }}</span>
              <span class="hot-views">{{ item.viewCount }}</span>
            </div>
            <el-empty v-if="hotNews.length === 0" description="暂无数据" :image-size="60" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="16" class="quick-row">
      <el-col :span="24">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">快捷操作</h3>
          </div>
          <div class="quick-actions">
            <div
              v-for="action in quickActions"
              :key="action.path"
              class="quick-item"
              @click="router.push(action.path)"
            >
              <el-icon :size="24" :color="action.color"><component :is="action.icon" /></el-icon>
              <span>{{ action.title }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()
const chartRef = ref(null)
const chartRange = ref('7')
let chartInstance = null

// 统计卡片（占位数据，后续接接口）
const statCards = ref([
  { title: '用户总数', value: 128, icon: 'User', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '资讯总数', value: 56, icon: 'Document', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '活动总数', value: 12, icon: 'Calendar', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { title: '报名总次数', value: 326, icon: 'Tickets', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

// 热门资讯（占位）
const hotNews = ref([
  { id: 1, title: '寿阳非遗传承人灵石之行：交流互鉴之光照亮非遗传承之路', viewCount: 356 },
  { id: 2, title: '【怡然见晋中 休闲寿阳游】非遗进景区展演活动热闹非凡', viewCount: 289 },
  { id: 3, title: '寿阳县庆祝新中国成立75周年群众文化活动启动', viewCount: 215 },
  { id: 4, title: '九九重阳 情暖夕阳——文化馆流动文化走进景尚敬老院', viewCount: 178 },
  { id: 5, title: '关于举办寿阳县2024年全民阅读活动的通知', viewCount: 142 }
])

// 快捷操作
const quickActions = [
  { title: '发布资讯', path: '/news', icon: 'EditPen', color: '#e57373' },
  { title: '添加活动', path: '/activity', icon: 'Plus', color: '#ffa726' },
  { title: '轮播图管理', path: '/banner', icon: 'Picture', color: '#ab47bc' },
  { title: '景点管理', path: '/scenic', icon: 'Location', color: '#66bb6a' },
  { title: '用户管理', path: '/user', icon: 'User', color: '#26a69a' },
  { title: '个人设置', path: '/profile', icon: 'Setting', color: '#78909c' }
]

// 加载图表数据（占位）
function loadChartData() {
  if (!chartInstance) return

  const days = chartRange.value === '7' ? 7 : 30
  const dates = []
  const values = []
  const today = new Date()

  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(today)
    d.setDate(d.getDate() - i)
    dates.push(`${d.getMonth() + 1}/${d.getDate()}`)
    values.push(Math.floor(Math.random() * 10) + 1)
  }

  chartInstance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '发布数量',
        type: 'line',
        smooth: true,
        data: values,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(44, 62, 107, 0.3)' },
            { offset: 1, color: 'rgba(44, 62, 107, 0.02)' }
          ])
        },
        lineStyle: { color: '#2c3e6b', width: 2 },
        itemStyle: { color: '#2c3e6b' }
      }
    ]
  })
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  loadChartData()
}

function handleResize() {
  chartInstance?.resize()
}

onMounted(() => {
  nextTick(() => {
    initChart()
  })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  padding: 16px;
}

.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;

  &:hover {
    transform: translateY(-3px);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .stat-info {
    .stat-value {
      font-size: 28px;
      font-weight: bold;
      line-height: 1.2;
    }

    .stat-title {
      font-size: 13px;
      opacity: 0.85;
      margin-top: 4px;
    }
  }
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  .chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.chart-container {
  height: 320px;
}

.hot-list {
  .hot-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }
  }

  .hot-rank {
    width: 22px;
    height: 22px;
    border-radius: 4px;
    background: #c0c4cc;
    color: #fff;
    font-size: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    &.top {
      background: #f56c6c;
    }
  }

  .hot-title {
    flex: 1;
    font-size: 13px;
    color: #606266;
  }

  .hot-views {
    font-size: 12px;
    color: #909399;
    flex-shrink: 0;
  }
}

.quick-row {
  .quick-actions {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 12px;
  }

  .quick-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 20px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid #ebeef5;

    &:hover {
      background: #f5f7fa;
      border-color: #2c3e6b;
      transform: translateY(-2px);
    }

    span {
      font-size: 13px;
      color: #606266;
    }
  }
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
