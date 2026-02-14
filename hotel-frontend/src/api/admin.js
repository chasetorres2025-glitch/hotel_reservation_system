import request from '@/utils/request'

// 管理端 - 用户管理API
export const adminUserApi = {
  // 获取用户列表
  getList(params) {
    return request.get('/admin/user/list', { params })
  },
  
  // 获取用户详情
  getById(id) {
    return request.get(`/admin/user/${id}`)
  },
  
  // 新增用户
  add(data) {
    return request.post('/admin/user', data)
  },
  
  // 修改用户
  update(data) {
    return request.put('/admin/user', data)
  },
  
  // 删除用户
  delete(id) {
    return request.delete(`/admin/user/${id}`)
  },
  
  // 修改用户状态
  updateStatus(data) {
    return request.put('/admin/user/status', data)
  },
  
  // 重置密码
  resetPassword(data) {
    return request.put('/admin/user/reset-password', data)
  }
}

// 管理端 - 客房管理API
export const adminRoomApi = {
  // 获取客房列表
  getList(params) {
    return request.get('/admin/room/list', { params })
  },
  
  // 获取客房详情
  getById(id) {
    return request.get(`/admin/room/${id}`)
  },
  
  // 新增客房
  add(data) {
    return request.post('/admin/room', data)
  },
  
  // 修改客房
  update(data) {
    return request.put('/admin/room', data)
  },
  
  // 删除客房
  delete(id) {
    return request.delete(`/admin/room/${id}`)
  },
  
  // 修改客房状态
  updateStatus(data) {
    return request.put('/admin/room/status', data)
  },
  
  // 获取客房类型列表
  getTypeList() {
    return request.get('/admin/room/type/list')
  }
}

// 管理端 - 订单管理API
export const adminOrderApi = {
  // 获取订单列表
  getList(params) {
    return request.get('/admin/order/list', { params })
  },
  
  // 获取订单详情
  getById(id) {
    return request.get(`/admin/order/${id}`)
  },
  
  // 确认订单
  confirm(id) {
    return request.put(`/admin/order/confirm/${id}`)
  },
  
  // 拒绝订单
  reject(id, data) {
    return request.put(`/admin/order/reject/${id}`, data)
  },
  
  // 办理入住
  checkIn(id) {
    return request.put(`/admin/order/check-in/${id}`)
  },
  
  // 办理退房
  checkOut(id) {
    return request.put(`/admin/order/check-out/${id}`)
  },
  
  // 导出订单
  export(params) {
    return request.get('/admin/order/export', { 
      params,
      responseType: 'blob'
    })
  }
}

// 管理端 - 数据统计API
export const adminStatsApi = {
  // 预订率统计
  getBookingRate(params) {
    return request.get('/admin/stats/booking-rate', { params })
  },
  
  // 营收统计
  getRevenue(params) {
    return request.get('/admin/stats/revenue', { params })
  },
  
  // 营收趋势
  getRevenueTrend(params) {
    return request.get('/admin/stats/revenue-trend', { params })
  },
  
  // 订单统计
  getOrderStats(params) {
    return request.get('/admin/stats/order', { params })
  }
}
