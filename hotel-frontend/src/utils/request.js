import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 可以在这里添加token等
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 根据code判断请求是否成功
    if (res.code === 200) {
      return res
    }
    
    // 处理业务错误
    const errorMsg = res.message || '请求失败'
    
    // 未登录或登录过期
    if (res.code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
      setTimeout(() => {
        window.location.href = '/user/login'
      }, 1500)
      return Promise.reject(new Error(errorMsg))
    }
    
    // 无权限
    if (res.code === 403) {
      ElMessage.error('无权限访问')
      return Promise.reject(new Error(errorMsg))
    }
    
    // 其他错误
    ElMessage.error(errorMsg)
    return Promise.reject(new Error(errorMsg))
  },
  error => {
    console.error('响应错误:', error)
    
    let message = '网络错误，请稍后重试'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未登录或登录已过期'
          localStorage.removeItem('userInfo')
          localStorage.removeItem('token')
          setTimeout(() => {
            window.location.href = '/user/login'
          }, 1500)
          break
        case 403:
          message = '无权限访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.message || '请求失败'
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
