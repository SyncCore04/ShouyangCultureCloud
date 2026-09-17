/**
 * 文旅服务模块接口封装（活动/票务/场馆）
 */
import { get, post } from '@/utils/request'

// ==================== 活动 ====================

/**
 * 获取活动列表
 * @param {Object} params - { page, size, status, keyword }
 */
export function getActivityList(params) {
  return get('/activity/list', params)
}

/**
 * 获取活动详情
 * @param {number} id - 活动ID
 * @returns {Object} { activity, isRegistered }
 */
export function getActivityDetail(id) {
  return get(`/activity/${id}`)
}

/**
 * 活动报名
 * @param {Object} data - { activityId, name, phone, remark }
 */
export function registerActivity(data) {
  return post('/activity/register', data)
}

/**
 * 我的报名列表
 * @param {Object} params - { page, size }
 */
export function getMyActivityList(params) {
  return get('/activity/my/list', params)
}

// ==================== 票务 ====================

/**
 * 获取票务列表
 * @param {Object} params - { page, size, keyword }
 */
export function getTicketList(params) {
  return get('/ticket/list', params)
}

/**
 * 获取票务详情
 * @param {number} id - 票务ID
 */
export function getTicketDetail(id) {
  return get(`/ticket/${id}`)
}

// ==================== 场馆 ====================

/**
 * 获取场馆列表
 * @param {Object} params - { page, size, keyword }
 */
export function getVenueList(params) {
  return get('/venue/list', params)
}

/**
 * 获取场馆详情
 * @param {number} id - 场馆ID
 */
export function getVenueDetail(id) {
  return get(`/venue/${id}`)
}
