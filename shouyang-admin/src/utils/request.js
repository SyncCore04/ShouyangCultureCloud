import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const TOKEN_KEY = 'shouyang_admin_token'
const USER_KEY = 'shouyang_admin_user'

// Token 管理
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}
export function getAdminUser() {
  const str = localStorage.getItem(USER_KEY)
  return str ? JSON.parse(str) : null
}
export function setAdminUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}
export function clearAuth() {
  removeToken()
  localStorage.removeItem(USER_KEY)
}

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (response.config.responseType === 'blob') return response

    if (res.code === 200) return res

    // 401 未登录
    if (res.code === 401 || res.code === 1005 || res.code === 1006) {
      ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
        confirmButtonText: '重新登录',
        showCancelButton: false,
        type: 'warning'
      }).then(() => {
        clearAuth()
        window.location.href = '/login'
      })
      return Promise.reject(new Error(res.message))
    }

    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    if (!error.response) {
      ElMessage.error('网络连接失败')
    } else {
      const status = error.response.status
      if (status === 401) {
        clearAuth()
        window.location.href = '/login'
      } else {
        ElMessage.error(error.response.data?.message || `请求失败 (${status})`)
      }
    }
    return Promise.reject(error)
  }
)

export function get(url, params) {
  return service.get(url, { params })
}
export function post(url, data) {
  return service.post(url, data)
}
export function put(url, data) {
  return service.put(url, data)
}
export function del(url, params) {
  return service.delete(url, { params })
}

export default service
