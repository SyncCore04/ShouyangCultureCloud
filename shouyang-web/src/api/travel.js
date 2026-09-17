/**
 * 畅游寿阳模块接口封装（景点/美食/酒店/攻略）
 */
import { get } from '@/utils/request'

// ==================== 景点 ====================

/**
 * 获取景点列表
 * @param {Object} params - { page, size, keyword, level }
 */
export function getScenicList(params) {
  return get('/scenic/list', params)
}

/**
 * 获取景点详情
 * @param {number} id - 景点ID
 */
export function getScenicDetail(id) {
  return get(`/scenic/${id}`)
}

// ==================== 美食 ====================

/**
 * 获取美食列表
 * @param {Object} params - { page, size, keyword, category }
 */
export function getFoodList(params) {
  return get('/food/list', params)
}

/**
 * 获取美食详情
 * @param {number} id - 美食ID
 */
export function getFoodDetail(id) {
  return get(`/food/${id}`)
}

// ==================== 酒店 ====================

/**
 * 获取酒店列表
 * @param {Object} params - { page, size, keyword, star }
 */
export function getHotelList(params) {
  return get('/hotel/list', params)
}

/**
 * 获取酒店详情
 * @param {number} id - 酒店ID
 */
export function getHotelDetail(id) {
  return get(`/hotel/${id}`)
}

// ==================== 攻略 ====================

/**
 * 获取攻略列表
 * @param {Object} params - { page, size, keyword }
 */
export function getGuideList(params) {
  return get('/guide/list', params)
}

/**
 * 获取攻略详情
 * @param {number} id - 攻略ID
 */
export function getGuideDetail(id) {
  return get(`/guide/${id}`)
}

// ==================== 通用方法（按 type 分发） ====================

/**
 * 类型配置映射
 */
export const typeConfig = {
  scenic: {
    name: '景点推荐',
    listApi: getScenicList,
    detailApi: getScenicDetail,
    listParam: 'level',
    filterOptions: [
      { label: '全部', value: '' },
      { label: '5A', value: '5A' },
      { label: '4A', value: '4A' },
      { label: '3A', value: '3A' }
    ]
  },
  food: {
    name: '特色美食',
    listApi: getFoodList,
    detailApi: getFoodDetail,
    listParam: 'category',
    filterOptions: [
      { label: '全部', value: '' },
      { label: '主食', value: '主食' },
      { label: '小吃', value: '小吃' },
      { label: '菜肴', value: '菜肴' },
      { label: '甜品', value: '甜品' }
    ]
  },
  hotel: {
    name: '民宿酒店',
    listApi: getHotelList,
    detailApi: getHotelDetail,
    listParam: 'star',
    filterOptions: [
      { label: '全部', value: '' },
      { label: '五星级', value: 5 },
      { label: '四星级', value: 4 },
      { label: '三星级', value: 3 },
      { label: '二星级', value: 2 }
    ]
  },
  guide: {
    name: '旅游攻略',
    listApi: getGuideList,
    detailApi: getGuideDetail,
    listParam: null,
    filterOptions: []
  }
}

/**
 * 根据 type 获取列表
 */
export function getTravelList(type, params) {
  const config = typeConfig[type]
  if (!config) return Promise.reject(new Error('未知类型: ' + type))
  return config.listApi(params)
}

/**
 * 根据 type 获取详情
 */
export function getTravelDetail(type, id) {
  const config = typeConfig[type]
  if (!config) return Promise.reject(new Error('未知类型: ' + type))
  return config.detailApi(id)
}
