import { get, put, del } from '@/utils/request'

// 用户列表
export function getUserList(params) {
  return get('/admin/user/list', params)
}

// 用户详情
export function getUserDetail(id) {
  return get(`/admin/user/${id}`)
}

// 启用/禁用用户
export function updateUserStatus(id, status) {
  return put('/admin/user/status', { id, status })
}

// 删除用户
export function deleteUser(id) {
  return del(`/admin/user/${id}`)
}

// 重置用户密码
export function resetUserPassword(id) {
  return put('/admin/user/password/reset', { id })
}
