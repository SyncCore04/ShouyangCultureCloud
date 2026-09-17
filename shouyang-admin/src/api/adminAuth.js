import { get, put } from '@/utils/request'

// 获取管理员信息
export function getAdminInfo() {
  return get('/admin/info')
}

// 修改管理员密码
export function updateAdminPassword(data) {
  return put('/admin/password', data)
}
