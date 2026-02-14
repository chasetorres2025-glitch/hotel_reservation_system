import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(localStorage.getItem('token') || '')
  
  // 计算属性
  const isLoggedIn = computed(() => !!userInfo.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const username = computed(() => userInfo.value?.username || '')
  const realName = computed(() => userInfo.value?.realName || '')
  
  // 方法
  // 登录
  async function login(loginData) {
    try {
      const res = await userApi.login(loginData)
      if (res.code === 200) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        return { success: true }
      }
      return { success: false, message: res.message }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }
  
  // 注册
  async function register(registerData) {
    try {
      const res = await userApi.register(registerData)
      if (res.code === 200) {
        return { success: true }
      }
      return { success: false, message: res.message }
    } catch (error) {
      return { success: false, message: error.message || '注册失败' }
    }
  }
  
  // 获取用户信息
  async function getUserInfo() {
    try {
      const res = await userApi.getUserInfo()
      if (res.code === 200) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        return { success: true }
      }
      return { success: false }
    } catch (error) {
      return { success: false }
    }
  }
  
  // 退出登录
  async function logout() {
    try {
      await userApi.logout()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      userInfo.value = null
      localStorage.removeItem('userInfo')
    }
  }
  
  // 更新用户信息
  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  // 清除用户信息
  function clearUserInfo() {
    userInfo.value = null
    localStorage.removeItem('userInfo')
  }
  
  return {
    userInfo,
    token,
    isLoggedIn,
    isAdmin,
    username,
    realName,
    login,
    register,
    getUserInfo,
    logout,
    setUserInfo,
    clearUserInfo
  }
})
