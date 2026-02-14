<template>
  <div class="order-manage">
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-icon">
            <el-icon :size="28"><Document /></el-icon>
          </div>
          <div class="header-text">
            <h2 class="page-title">订单管理</h2>
            <p class="page-desc">管理所有预订订单，处理入住和退房流程</p>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleExport" class="export-btn">
            <el-icon><Download /></el-icon>
            导出订单
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="stat-cards">
      <div class="stat-card total">
        <div class="card-icon">
          <el-icon :size="24"><Tickets /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ orderStats.total }}</div>
          <div class="card-label">总订单数</div>
        </div>
      </div>
      <div class="stat-card pending">
        <div class="card-icon">
          <el-icon :size="24"><Clock /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ orderStats.pending }}</div>
          <div class="card-label">待确认</div>
        </div>
      </div>
      <div class="stat-card confirmed">
        <div class="card-icon">
          <el-icon :size="24"><CircleCheck /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ orderStats.confirmed }}</div>
          <div class="card-label">已确认</div>
        </div>
      </div>
      <div class="stat-card checkedin">
        <div class="card-icon">
          <el-icon :size="24"><Key /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ orderStats.checkedin }}</div>
          <div class="card-label">已入住</div>
        </div>
      </div>
      <div class="stat-card completed">
        <div class="card-icon">
          <el-icon :size="24"><Finished /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ orderStats.completed }}</div>
          <div class="card-label">已完成</div>
        </div>
      </div>
    </div>
    
    <div class="search-form">
      <div class="search-header">
        <el-icon class="search-icon"><Search /></el-icon>
        <span class="search-title">筛选条件</span>
      </div>
      <el-form :model="queryParams" inline class="search-content">
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable prefix-icon="Tickets" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable prefix-icon="User" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待支付" :value="0" />
            <el-option label="待确认" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="已入住" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
            <el-option label="已拒绝" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期">
          <el-date-picker
            v-model="queryParams.checkInDate"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item class="search-buttons">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 订单列表 -->
    <div class="table-container">
      <div class="table-header">
        <span class="table-title">订单列表</span>
        <span class="table-count">共 {{ total }} 条记录</span>
      </div>
      <el-table :data="orderList" v-loading="loading" class="custom-table">
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <div class="order-no">
              <el-icon class="order-icon"><Tickets /></el-icon>
              <span>{{ row.orderNo }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="28" class="user-avatar">
                {{ row.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roomNumber" label="房间号" width="100">
          <template #default="{ row }">
            <span class="room-badge">{{ row.roomNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="房型" width="110">
          <template #default="{ row }">
            <div class="type-tag">
              <el-icon><HomeFilled /></el-icon>
              <span>{{ row.typeName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="入住日期" width="220">
          <template #default="{ row }">
            <div class="date-range">
              <div class="date-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ row.checkInDate }}</span>
              </div>
              <div class="date-arrow">→</div>
              <div class="date-item">
                <span>{{ row.checkOutDate }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="130">
          <template #default="{ row }">
            <div class="price-cell">
              <span class="price">¥{{ row.totalAmount }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <div class="status-badge" :class="getStatusClass(row.status)">
              <span class="status-dot"></span>
              <span>{{ getStatusText(row.status) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            <div class="time-cell">
              <el-icon><Clock /></el-icon>
              <span>{{ row.createTime }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleDetail(row)" class="action-btn detail">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <template v-if="row.status === 1">
                <el-button type="success" size="small" @click="handleConfirm(row)" class="action-btn confirm">
                  <el-icon><Select /></el-icon>
                  确认
                </el-button>
                <el-button type="danger" size="small" @click="handleReject(row)" class="action-btn reject">
                  <el-icon><CloseBold /></el-icon>
                  拒绝
                </el-button>
              </template>
              <el-button v-if="row.status === 2" type="primary" size="small" @click="handleCheckIn(row)" class="action-btn checkin">
                <el-icon><Key /></el-icon>
                入住
              </el-button>
              <el-button v-if="row.status === 3" type="success" size="small" @click="handleCheckOut(row)" class="action-btn checkout">
                <el-icon><Finished /></el-icon>
                退房
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </div>
    
    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="680px" class="detail-dialog">
      <div class="detail-content">
        <div class="detail-header">
          <div class="order-info">
            <span class="order-label">订单号</span>
            <span class="order-value">{{ orderDetail.orderNo }}</span>
          </div>
          <div class="status-badge" :class="getStatusClass(orderDetail.status)">
            <span class="status-dot"></span>
            <span>{{ getStatusText(orderDetail.status) }}</span>
          </div>
        </div>
        
        <div class="detail-sections">
          <div class="detail-section">
            <div class="section-title">
              <el-icon><User /></el-icon>
              <span>客户信息</span>
            </div>
            <div class="section-content">
              <div class="info-row">
                <span class="info-label">用户名</span>
                <span class="info-value">{{ orderDetail.username }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">联系人</span>
                <span class="info-value">{{ orderDetail.contactName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">联系电话</span>
                <span class="info-value">{{ orderDetail.contactPhone }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">入住人数</span>
                <span class="info-value">{{ orderDetail.guestCount }}人</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <div class="section-title">
              <el-icon><HomeFilled /></el-icon>
              <span>房间信息</span>
            </div>
            <div class="section-content">
              <div class="info-row">
                <span class="info-label">房间号</span>
                <span class="info-value room-num">{{ orderDetail.roomNumber }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">房型</span>
                <span class="info-value">{{ orderDetail.typeName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">入住日期</span>
                <span class="info-value">{{ orderDetail.checkInDate }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">退房日期</span>
                <span class="info-value">{{ orderDetail.checkOutDate }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <div class="section-title">
              <el-icon><Money /></el-icon>
              <span>费用信息</span>
            </div>
            <div class="section-content">
              <div class="info-row">
                <span class="info-label">订单金额</span>
                <span class="info-value price">¥{{ orderDetail.totalAmount }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">已支付金额</span>
                <span class="info-value price">¥{{ orderDetail.paidAmount }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section full-width">
            <div class="section-title">
              <el-icon><Document /></el-icon>
              <span>其他信息</span>
            </div>
            <div class="section-content">
              <div class="info-row">
                <span class="info-label">特殊要求</span>
                <span class="info-value">{{ orderDetail.specialRequest || '无' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">备注</span>
                <span class="info-value">{{ orderDetail.remark || '无' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">创建时间</span>
                <span class="info-value">{{ orderDetail.createTime }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ orderDetail.updateTime }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog v-model="rejectVisible" title="拒绝订单" width="440px" class="reject-dialog">
      <div class="reject-content">
        <div class="reject-warning">
          <el-icon class="warning-icon"><Warning /></el-icon>
          <span>请填写拒绝原因，该信息将通知用户</span>
        </div>
        <el-form :model="rejectForm" label-width="80px">
          <el-form-item label="拒绝原因">
            <el-input v-model="rejectForm.remark" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rejectVisible = false">取消</el-button>
          <el-button type="danger" @click="submitReject" :loading="rejectLoading">确认拒绝</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Download, Search, Refresh, View, Select, CloseBold, Key, Finished,
  Document, Tickets, Clock, CircleCheck, User, HomeFilled, Calendar, Money, Warning
} from '@element-plus/icons-vue'
import { adminOrderApi } from '@/api/admin'

const loading = ref(false)
const rejectLoading = ref(false)
const orderList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const rejectVisible = ref(false)
const orderDetail = ref({})
const currentOrderId = ref(null)

const orderStats = computed(() => {
  const stats = { total: 0, pending: 0, confirmed: 0, checkedin: 0, completed: 0 }
  orderList.value.forEach(order => {
    stats.total++
    if (order.status === 1) stats.pending++
    else if (order.status === 2) stats.confirmed++
    else if (order.status === 3) stats.checkedin++
    else if (order.status === 4) stats.completed++
  })
  return stats
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  username: '',
  status: '',
  checkInDate: null
})

const rejectForm = reactive({
  remark: ''
})

// 状态映射
const statusMap = {
  0: { text: '待支付', type: 'warning' },
  1: { text: '待确认', type: 'warning' },
  2: { text: '已确认', type: 'primary' },
  3: { text: '已入住', type: 'primary' },
  4: { text: '已完成', type: 'success' },
  5: { text: '已取消', type: 'info' },
  6: { text: '已拒绝', type: 'danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'
const getStatusClass = (status) => {
  const classes = {
    0: 'pending-payment',
    1: 'pending',
    2: 'confirmed',
    3: 'checkedin',
    4: 'completed',
    5: 'cancelled',
    6: 'rejected'
  }
  return classes[status] || 'pending'
}

// 方法
const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (params.checkInDate && params.checkInDate.length === 2) {
      params.startDate = params.checkInDate[0]
      params.endDate = params.checkInDate[1]
      delete params.checkInDate
    }
    const res = await adminOrderApi.getList(params)
    if (res.code === 200) {
      orderList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

const handleReset = () => {
  queryParams.orderNo = ''
  queryParams.username = ''
  queryParams.status = ''
  queryParams.checkInDate = null
  handleSearch()
}

const handleDetail = async (row) => {
  try {
    const res = await adminOrderApi.getById(row.id)
    if (res.code === 200) {
      orderDetail.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
  }
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确定要确认该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await adminOrderApi.confirm(row.id)
    if (res.code === 200) {
      ElMessage.success('订单确认成功')
      getList()
    }
  } catch (error) {
    // 用户取消
  }
}

const handleReject = (row) => {
  currentOrderId.value = row.id
  rejectForm.remark = ''
  rejectVisible.value = true
}

const submitReject = async () => {
  rejectLoading.value = true
  try {
    const res = await adminOrderApi.reject(currentOrderId.value, rejectForm)
    if (res.code === 200) {
      ElMessage.success('订单已拒绝')
      rejectVisible.value = false
      getList()
    }
  } catch (error) {
    console.error('拒绝订单失败:', error)
  } finally {
    rejectLoading.value = false
  }
}

const handleCheckIn = async (row) => {
  try {
    await ElMessageBox.confirm('确定要办理入住吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await adminOrderApi.checkIn(row.id)
    if (res.code === 200) {
      ElMessage.success('入住办理成功')
      getList()
    }
  } catch (error) {
    // 用户取消
  }
}

const handleCheckOut = async (row) => {
  try {
    await ElMessageBox.confirm('确定要办理退房吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await adminOrderApi.checkOut(row.id)
    if (res.code === 200) {
      ElMessage.success('退房办理成功')
      getList()
    }
  } catch (error) {
    // 用户取消
  }
}

const handleExport = async () => {
  try {
    const res = await adminOrderApi.export(queryParams)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `订单数据_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.order-manage {
  .page-header {
    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
    border-radius: 16px;
    padding: 24px 32px;
    margin-bottom: 24px;
    box-shadow: 0 10px 30px rgba(245, 158, 11, 0.3);
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    
    .header-icon {
      width: 56px;
      height: 56px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
    }
    
    .header-text {
      .page-title {
        font-size: 24px;
        font-weight: 700;
        color: white;
        margin: 0 0 4px 0;
      }
      
      .page-desc {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
        margin: 0;
      }
    }
    
    .header-actions {
      .export-btn {
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        font-weight: 600;
        padding: 12px 24px;
        border-radius: 10px;
        transition: all 0.3s ease;
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
        }
      }
    }
  }
  
  .stat-cards {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 16px;
    margin-bottom: 24px;
    
    @media (max-width: 1400px) {
      grid-template-columns: repeat(3, 1fr);
    }
    
    @media (max-width: 900px) {
      grid-template-columns: repeat(2, 1fr);
    }
    
    @media (max-width: 600px) {
      grid-template-columns: 1fr;
    }
    
    .stat-card {
      background: white;
      border-radius: 12px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }
      
      .card-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
      }
      
      .card-content {
        .card-value {
          font-size: 24px;
          font-weight: 700;
          color: #1f2937;
          line-height: 1.2;
        }
        
        .card-label {
          font-size: 13px;
          color: #6b7280;
          margin-top: 4px;
        }
      }
      
      &.total .card-icon {
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
      }
      
      &.pending .card-icon {
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
      }
      
      &.confirmed .card-icon {
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      }
      
      &.checkedin .card-icon {
        background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
      }
      
      &.completed .card-icon {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      }
    }
  }
  
  .search-form {
    background: white;
    border-radius: 12px;
    padding: 20px 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    
    .search-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #f3f4f6;
      
      .search-icon {
        color: #f59e0b;
        font-size: 18px;
      }
      
      .search-title {
        font-size: 15px;
        font-weight: 600;
        color: #374151;
      }
    }
    
    .search-content {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      
      :deep(.el-form-item) {
        margin-bottom: 0;
        margin-right: 0;
      }
      
      :deep(.el-input),
      :deep(.el-select) {
        width: 200px;
      }
      
      .search-buttons {
        margin-left: auto;
      }
    }
  }
  
  .table-container {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 16px;
      border-bottom: 1px solid #f3f4f6;
      
      .table-title {
        font-size: 16px;
        font-weight: 600;
        color: #1f2937;
      }
      
      .table-count {
        font-size: 13px;
        color: #6b7280;
      }
    }
    
    .custom-table {
      :deep(.el-table__header) {
        th {
          background: #f9fafb;
          color: #374151;
          font-weight: 600;
        }
      }
      
      .order-no {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .order-icon {
          color: #f59e0b;
        }
        
        span {
          font-weight: 600;
          color: #1f2937;
          font-size: 13px;
        }
      }
      
      .user-cell {
        display: flex;
        align-items: center;
        gap: 10px;
        
        .user-avatar {
          background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
          color: white;
          font-weight: 600;
          font-size: 12px;
        }
        
        .username {
          font-weight: 500;
          color: #1f2937;
        }
      }
      
      .room-badge {
        display: inline-block;
        padding: 4px 12px;
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
        color: white;
        border-radius: 6px;
        font-size: 13px;
        font-weight: 600;
      }
      
      .type-tag {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 4px 12px;
        background: #f3f4f6;
        border-radius: 6px;
        font-size: 13px;
        color: #374151;
        
        .el-icon {
          color: #f59e0b;
        }
      }
      
      .date-range {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .date-item {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 13px;
          color: #374151;
          
          .el-icon {
            color: #9ca3af;
          }
        }
        
        .date-arrow {
          color: #9ca3af;
          font-size: 12px;
        }
      }
      
      .price-cell {
        .price {
          font-size: 16px;
          font-weight: 700;
          color: #ef4444;
        }
      }
      
      .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;
        
        .status-dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
        }
        
        &.pending-payment {
          background: #fef3c7;
          color: #92400e;
          
          .status-dot {
            background: #f59e0b;
          }
        }
        
        &.pending {
          background: #fef3c7;
          color: #92400e;
          
          .status-dot {
            background: #f59e0b;
          }
        }
        
        &.confirmed {
          background: #dbeafe;
          color: #1e40af;
          
          .status-dot {
            background: #3b82f6;
          }
        }
        
        &.checkedin {
          background: #ede9fe;
          color: #5b21b6;
          
          .status-dot {
            background: #8b5cf6;
          }
        }
        
        &.completed {
          background: #d1fae5;
          color: #065f46;
          
          .status-dot {
            background: #10b981;
          }
        }
        
        &.cancelled {
          background: #f3f4f6;
          color: #374151;
          
          .status-dot {
            background: #6b7280;
          }
        }
        
        &.rejected {
          background: #fee2e2;
          color: #991b1b;
          
          .status-dot {
            background: #ef4444;
          }
        }
      }
      
      .time-cell {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #6b7280;
        font-size: 13px;
        
        .el-icon {
          color: #9ca3af;
        }
      }
      
      .action-buttons {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        
        .action-btn {
          padding: 6px 12px;
          border-radius: 6px;
          font-size: 12px;
          font-weight: 500;
          transition: all 0.2s ease;
          
          &.detail {
            background: #eff6ff;
            border-color: #3b82f6;
            color: #3b82f6;
            
            &:hover {
              background: #3b82f6;
              color: white;
            }
          }
          
          &.confirm {
            background: #d1fae5;
            border-color: #10b981;
            color: #10b981;
            
            &:hover {
              background: #10b981;
              color: white;
            }
          }
          
          &.reject {
            background: #fee2e2;
            border-color: #ef4444;
            color: #ef4444;
            
            &:hover {
              background: #ef4444;
              color: white;
            }
          }
          
          &.checkin {
            background: #ede9fe;
            border-color: #8b5cf6;
            color: #8b5cf6;
            
            &:hover {
              background: #8b5cf6;
              color: white;
            }
          }
          
          &.checkout {
            background: #d1fae5;
            border-color: #10b981;
            color: #10b981;
            
            &:hover {
              background: #10b981;
              color: white;
            }
          }
        }
      }
    }
    
    .pagination-container {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #f3f4f6;
    }
  }
  
  .detail-dialog {
    :deep(.el-dialog__header) {
      padding: 20px 24px;
      border-bottom: 1px solid #f3f4f6;
      margin: 0;
      
      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: #1f2937;
      }
    }
    
    :deep(.el-dialog__body) {
      padding: 0;
    }
    
    .detail-content {
      .detail-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 24px;
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
        
        .order-info {
          display: flex;
          flex-direction: column;
          gap: 4px;
          
          .order-label {
            font-size: 12px;
            color: rgba(255, 255, 255, 0.8);
          }
          
          .order-value {
            font-size: 16px;
            font-weight: 600;
            color: white;
          }
        }
        
        .status-badge {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          padding: 8px 16px;
          border-radius: 20px;
          font-size: 13px;
          font-weight: 500;
          background: rgba(255, 255, 255, 0.2);
          color: white;
          
          .status-dot {
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background: white;
          }
        }
      }
      
      .detail-sections {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 0;
        
        .detail-section {
          padding: 20px 24px;
          border-bottom: 1px solid #f3f4f6;
          
          &:nth-child(odd) {
            border-right: 1px solid #f3f4f6;
          }
          
          &.full-width {
            grid-column: 1 / -1;
            border-right: none;
          }
          
          .section-title {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #1f2937;
            
            .el-icon {
              color: #f59e0b;
            }
          }
          
          .section-content {
            .info-row {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 8px 0;
              
              &:not(:last-child) {
                border-bottom: 1px dashed #f3f4f6;
              }
              
              .info-label {
                font-size: 13px;
                color: #6b7280;
              }
              
              .info-value {
                font-size: 13px;
                font-weight: 500;
                color: #1f2937;
                
                &.room-num {
                  color: #f59e0b;
                  font-weight: 600;
                }
                
                &.price {
                  color: #ef4444;
                  font-weight: 700;
                  font-size: 15px;
                }
              }
            }
          }
        }
      }
    }
  }
  
  .reject-dialog {
    :deep(.el-dialog__header) {
      padding: 20px 24px;
      border-bottom: 1px solid #f3f4f6;
      margin: 0;
      
      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: #1f2937;
      }
    }
    
    :deep(.el-dialog__body) {
      padding: 24px;
    }
    
    :deep(.el-dialog__footer) {
      padding: 16px 24px;
      border-top: 1px solid #f3f4f6;
    }
    
    .reject-content {
      .reject-warning {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px 16px;
        background: #fef3c7;
        border-radius: 8px;
        margin-bottom: 20px;
        
        .warning-icon {
          color: #f59e0b;
          font-size: 20px;
        }
        
        span {
          font-size: 13px;
          color: #92400e;
        }
      }
    }
    
    .dialog-footer {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }
  }
}
</style>
