/**
 * 文旅时空模块接口封装（数字展馆/非遗文化/文创商城）
 */
import { get } from '@/utils/request'

// ==================== 数字展馆 ====================

/**
 * 获取数字展馆列表
 * @param {Object} params - { page, size }
 */
export function getPavilionList(params) {
  return get('/pavilion/list', params)
}

/**
 * 获取数字展馆详情
 * @param {number} id - 展馆ID
 */
export function getPavilionDetail(id) {
  return get(`/pavilion/${id}`)
}

// ==================== 非遗文化 ====================

/**
 * 获取非遗列表
 * @param {Object} params - { page, size, level, category, keyword }
 */
export function getHeritageList(params) {
  return get('/heritage/list', params)
}

/**
 * 获取非遗详情
 * @param {number} id - 非遗ID
 */
export function getHeritageDetail(id) {
  return get(`/heritage/${id}`)
}

// ==================== 文创商城 ====================

/**
 * 获取商品列表
 * @param {Object} params - { page, size, keyword, sort }
 */
export function getProductList(params) {
  return get('/product/list', params)
}

/**
 * 获取商品详情
 * @param {number} id - 商品ID
 */
export function getProductDetail(id) {
  return get(`/product/${id}`)
}

// ==================== 非遗级别配置 ====================

/**
 * 非遗级别配置（标签 + 颜色）
 */
export const heritageLevelConfig = {
  '国家级': { color: '#e53935', bg: '#ffebee' },
  '省级': { color: '#fb8c00', bg: '#fff3e0' },
  '市级': { color: '#1e88e5', bg: '#e3f2fd' },
  '县级': { color: '#43a047', bg: '#e8f5e9' }
}

/**
 * 获取非遗级别样式
 */
export function getLevelStyle(level) {
  return heritageLevelConfig[level] || { color: '#757575', bg: '#f5f5f5' }
}
