import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { post } from '@/utils/request'
import { getToken, setToken, getAdminUser, setAdminUser, clearAuth } from '@/utils/request'

export const useUserStore = defineStore('adminUser', () => {
  const token = ref(getToken() || '')
  const userInfo = ref(getAdminUser() || null)

  const isLogin = computed(() => !!token.value)
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '管理员')
  const role = computed(() => userInfo.value?.role || 'admin')
  const isSuper = computed(() => role.value === 'super')

  async function login(loginForm) {
    const res = await post('/admin/login', loginForm)
    const { token: newToken, userInfo: info } = res.data
    token.value = newToken
    userInfo.value = info
    setToken(newToken)
    setAdminUser(info)
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    clearAuth()
  }

  return { token, userInfo, isLogin, nickname, role, isSuper, login, logout }
})
