import request from '@/utils/request'

// 获取文旅单位列表
export const getOrgList = (params) => {
  return request.get('/org/list', { params })
}

// 获取文旅单位详情
export const getOrgDetail = (id) => {
  return request.get(`/org/${id}`)
}
