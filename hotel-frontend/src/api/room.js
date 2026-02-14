import request from '@/utils/request'

// 用户端 - 客房API
export const roomApi = {
  // 获取可预订客房列表
  getList(params) {
    return request.get('/user/room/list', { params })
  },
  
  // 获取客房详情
  getById(id) {
    return request.get(`/user/room/${id}`)
  },
  
  // 搜索可用客房
  search(data) {
    return request.post('/user/room/search', data)
  },
  
  // 获取客房类型列表
  getTypes() {
    return request.get('/user/room/type/list')
  }
}

// 用户端 - 订单API
export const orderApi = {
  // 提交预订订单
  create(data) {
    return request.post('/user/order', data)
  },
  
  // 获取我的订单列表
  getList(params) {
    return request.get('/user/order/list', { params })
  },
  
  // 获取订单详情
  getById(id) {
    return request.get(`/user/order/${id}`)
  },
  
  // 取消订单
  cancel(id) {
    return request.put(`/user/order/cancel/${id}`)
  },
  
  // 支付订单
  pay(id, data) {
    return request.post(`/user/order/pay/${id}`, data)
  }
}
