import { get } from '@/utils/request'

// 全站搜索
export function searchAll(params) {
  return get('/search', params)
}
