import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isShelter = computed(() => user.value?.role === 'SHELTER')
  const canManage = computed(() => isAdmin.value || isShelter.value)

  async function login(credentials) {
    const res = await loginApi(credentials)
    if (res.code === 200) {
      token.value = res.data.token
      user.value = res.data
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data))
    }
    return res
  }

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfo()
      if (res.code === 200) {
        user.value = { ...user.value, ...res.data }
        localStorage.setItem('user', JSON.stringify(user.value))
      }
    } catch (e) {
      logout()
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function updateUser(userData) {
    user.value = { ...user.value, ...userData }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    isShelter,
    canManage,
    login,
    logout,
    fetchUserInfo,
    updateUser
  }
})
