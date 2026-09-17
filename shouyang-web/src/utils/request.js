import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from './auth'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 如果有 token，添加到请求头
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data

    // 如果是文件下载（blob），直接返回
    if (response.config.responseType === 'blob') {
      return response
    }

    // code 为 200 表示成功
    if (res.code === 200) {
      return res
    }

    // 401 未登录或 token 过期
    if (res.code === 401 || res.code === 1005 || res.code === 1006) {
      ElMessage.error('登录已过期，请重新登录')
      clearAuth()
      // 跳转到登录页，携带当前路径用于登录后回跳
      router.push({
        path: '/login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
      return Promise.reject(new Error(res.message || '未登录'))
    }

    // 403 无权限
    if (res.code === 403) {
      ElMessage.error('无权限访问')
      return Promise.reject(new Error(res.message || '无权限'))
    }

    // 其他错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    console.error('响应错误:', error)

    // 网络错误
    if (!error.response) {
      ElMessage.error('网络连接失败，请检查网络')
      return Promise.reject(error)
    }

    // HTTP 状态码处理
    const status = error.response.status
    switch (status) {
      case 401:
        ElMessage.error('登录已过期，请重新登录')
        clearAuth()
        router.push('/login')
        break
      case 403:
        ElMessage.error('无权限访问')
        break
      case 404:
        ElMessage.error('请求资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error(error.response.data?.message || `请求失败 (${status})`)
    }

    return Promise.reject(error)
  }
)

/**
 * GET 请求
 */
export function get(url, params) {
  return service.get(url, { params })
}

/**
 * POST 请求
 */
export function post(url, data) {
  return service.post(url, data)
}

/**
 * PUT 请求
 */
export function put(url, data) {
  return service.put(url, data)
}

/**
 * DELETE 请求
 */
export function del(url, params) {
  return service.delete(url, { params })
}

export default service
