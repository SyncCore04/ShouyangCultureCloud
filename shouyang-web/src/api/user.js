/**
 * 用户中心接口封装
 */
import { get, post, put, del } from '@/utils/request'

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return get('/user/info')
}

/**
 * 更新用户信息
 * @param {Object} data - { nickname, avatar, phone, email, gender }
 */
export function updateUserInfo(data) {
  return put('/user/info', data)
}

/**
 * 修改密码
 * @param {Object} data - { oldPassword, newPassword }
 */
export function updatePassword(data) {
  return put('/user/password', data)
}

/**
 * 我的活动报名列表
 * @param {Object} params - { page, size }
 */
export function getMyActivityList(params) {
  return get('/user/activity/list', params)
}

/**
 * 取消活动报名
 * @param {number} registerId - 报名记录ID
 */
export function cancelActivity(registerId) {
  return del(`/user/activity/cancel/${registerId}`)
}

/**
 * 上传头像（使用管理员上传接口，用户也可使用）
 * @param {FormData} formData - 包含 file 字段
 */
export function uploadAvatar(formData) {
  return post('/admin/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
