import { get } from '@/utils/request'

// 仪表盘统计数据
export function getDashboardStats() {
  return get('/admin/dashboard/stats')
}

// 资讯发布趋势
export function getDashboardTrend(days = 7) {
  return get('/admin/dashboard/trend', { days })
}

// 热门资讯
export function getHotNews(limit = 10) {
  return get('/admin/dashboard/hot-news', { limit })
}
