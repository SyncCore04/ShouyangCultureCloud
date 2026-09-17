import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken, getUser, setUser, removeUser, clearAuth } from '@/utils/auth'
import { post, get } from '@/utils/request'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(getToken() || '')
  const userInfo = ref(getUser() || null)

  // 计算属性
  const isLogin = computed(() => !!token.value)
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '游客')
  const avatar = computed(() => userInfo.value?.avatar || '')

  /**
   * 用户登录
   */
  async function login(loginForm) {
    const res = await post('/user/login', loginForm)
    const { token: newToken, userInfo: info } = res.data
    token.value = newToken
    userInfo.value = info
    setToken(newToken)
    setUser(info)
    return res
  }

  /**
   * 用户注册
   */
  async function register(registerForm) {
    const res = await post('/user/register', registerForm)
    return res
  }

  /**
   * 获取当前用户信息
   */
  async function fetchUserInfo() {
    const res = await get('/user/info')
    userInfo.value = res.data
    setUser(res.data)
    return res
  }

  /**
   * 更新用户信息
   */
  async function updateUserInfo(data) {
    const res = await (await import('@/utils/request')).put('/user/info', data)
    userInfo.value = { ...userInfo.value, ...data }
    setUser(userInfo.value)
    return res
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    userInfo.value = null
    clearAuth()
  }

  return {
    token,
    userInfo,
    isLogin,
    nickname,
    avatar,
    login,
    register,
    fetchUserInfo,
    updateUserInfo,
    logout
  }
})
