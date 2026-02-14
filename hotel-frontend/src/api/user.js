import request from '@/utils/request'

// 用户认证相关API
export const userApi = {
  // 用户登录
  login(data) {
    return request.post('/user/login', data)
  },
  
  // 用户注册
  register(data) {
    return request.post('/user/register', data)
  },
  
  // 退出登录
  logout() {
    return request.post('/user/logout')
  },
  
  // 获取当前用户信息
  getUserInfo() {
    return request.get('/user/info')
  },
  
  // 获取个人信息
  getProfile() {
    return request.get('/user/profile')
  },
  
  // 修改个人信息
  updateProfile(data) {
    return request.put('/user/profile', data)
  },
  
  // 修改密码
  updatePassword(data) {
    return request.put('/user/password', data)
  }
}
