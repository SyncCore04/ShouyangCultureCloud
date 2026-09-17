/**
 * 收藏接口封装
 */
import { get, post, del } from '@/utils/request'

/**
 * 添加收藏
 * @param {string} targetType - 收藏类型（news/scenic/food/hotel/activity/product）
 * @param {number} targetId - 目标ID
 */
export function addFavorite(targetType, targetId) {
  return post('/favorite/add', { targetType, targetId })
}

/**
 * 取消收藏
 * @param {string} targetType - 收藏类型
 * @param {number} targetId - 目标ID
 */
export function deleteFavorite(targetType, targetId) {
  return del('/favorite/delete', { targetType, targetId })
}

/**
 * 我的收藏列表
 * @param {Object} params - { page, size, targetType }
 */
export function getFavoriteList(params) {
  return get('/favorite/list', params)
}

/**
 * 检查是否已收藏
 * @param {string} targetType - 收藏类型
 * @param {number} targetId - 目标ID
 */
export function checkFavorite(targetType, targetId) {
  return get('/favorite/check', { targetType, targetId })
}

/**
 * 收藏类型配置
 */
export const favoriteTypeConfig = {
  news: { label: '资讯', route: '/news/detail' },
  scenic: { label: '景点', route: '/travel/scenic' },
  food: { label: '美食', route: '/travel/food' },
  hotel: { label: '酒店', route: '/travel/hotel' },
  activity: { label: '活动', route: '/service/activity' },
  product: { label: '文创', route: '/culture/product' }
}
