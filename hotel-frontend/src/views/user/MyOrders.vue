<template>
  <div class="my-orders">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2 class="page-title">我的订单</h2>
          <p class="page-subtitle">查看和管理您的所有预订订单</p>
        </div>
        <div class="header-stats" v-if="!loading && total > 0">
          <div class="stat-item">
            <span class="stat-value">{{ total }}</span>
            <span class="stat-label">全部订单</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="filter-tabs">
      <el-radio-group v-model="currentStatus" @change="handleFilter" class="status-filter">
        <el-radio-button label="">
          <el-icon><List /></el-icon>
          <span>全部</span>
        </el-radio-button>
        <el-radio-button label="0">
          <el-badge :value="statusCounts.pending" :hidden="!statusCounts.pending" :max="99">
            <span>待支付</span>
          </el-badge>
        </el-radio-button>
        <el-radio-button label="1">
          <span>待确认</span>
        </el-radio-button>
        <el-radio-button label="2">
          <span>已确认</span>
        </el-radio-button>
        <el-radio-button label="3">
          <span>已入住</span>
        </el-radio-button>
        <el-radio-button label="4">
          <span>已完成</span>
        </el-radio-button>
        <el-radio-button label="5">
          <span>已取消</span>
        </el-radio-button>
      </el-radio-group>
    </div>
    
    <div class="order-list" v-loading="loading">
      <transition-group name="order-fade" tag="div" class="orders-container">
        <div class="order-card" v-for="order in orderList" :key="order.id">
          <div class="order-header">
            <div class="order-info">
              <div class="order-no-wrapper">
                <span class="order-no-label">订单号</span>
                <span class="order-no-value">{{ order.orderNo }}</span>
              </div>
              <div class="order-time">
                <el-icon><Clock /></el-icon>
                <span>{{ order.createTime }}</span>
              </div>
            </div>
            <div class="order-status" :class="'status-' + order.status">
              <span class="status-dot"></span>
              <span class="status-text">{{ getStatusText(order.status) }}</span>
            </div>
          </div>
          
          <div class="order-content">
            <div class="room-info">
              <div class="room-image">
                <img :src="order.roomImage || defaultImage" :alt="order.typeName" />
                <div class="image-overlay">
                  <el-icon><Picture /></el-icon>
                </div>
              </div>
              <div class="room-detail">
                <h4 class="room-title">{{ order.roomNumber }} - {{ order.typeName }}</h4>
                <div class="info-item">
                  <el-icon class="info-icon"><Calendar /></el-icon>
                  <span class="info-text">
                    <span class="date-highlight">{{ order.checkInDate }}</span>
                    <span class="date-separator">至</span>
                    <span class="date-highlight">{{ order.checkOutDate }}</span>
                    <span class="days-badge">共{{ getDaysCount(order.checkInDate, order.checkOutDate) }}晚</span>
                  </span>
                </div>
                <div class="info-item">
                  <el-icon class="info-icon"><User /></el-icon>
                  <span class="info-text">{{ order.contactName }} · {{ order.contactPhone }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-price">
              <div class="price-label">订单金额</div>
              <div class="price-value">
                <span class="price-symbol">¥</span>
                <span class="price-amount">{{ order.totalAmount }}</span>
              </div>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="order-actions">
              <el-button class="action-btn detail-btn" @click="handleDetail(order)">
                <el-icon><View /></el-icon>
                <span>查看详情</span>
              </el-button>
              <el-button
                v-if="order.status === 0"
                type="primary"
                class="action-btn pay-btn"
                @click="handlePay(order)"
              >
                <el-icon><Wallet /></el-icon>
                <span>立即支付</span>
              </el-button>
              <el-button
                v-if="order.status === 0 || order.status === 1"
                type="danger"
                plain
                class="action-btn cancel-btn"
                @click="handleCancel(order)"
              >
                <el-icon><Close /></el-icon>
                <span>取消订单</span>
              </el-button>
            </div>
          </div>
        </div>
      </transition-group>
      
      <div class="empty-state" v-if="!loading && orderList.length === 0">
        <el-empty>
          <template #image>
            <div class="empty-icon">
              <el-icon :size="80"><Document /></el-icon>
            </div>
          </template>
          <template #description>
            <div class="empty-content">
              <h3>暂无订单</h3>
              <p>您还没有任何预订订单</p>
              <el-button type="primary" @click="router.push('/user/rooms')">
                <el-icon><OfficeBuilding /></el-icon>
                去预订房间
              </el-button>
            </div>
          </template>
        </el-empty>
      </div>
      
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="getList"
          @current-change="getList"
          background
        />
      </div>
    </div>
    
    <el-dialog 
      v-model="detailVisible" 
      title="订单详情" 
      width="680px"
      class="detail-dialog"
      destroy-on-close
    >
      <div class="detail-content">
        <div class="detail-header">
          <div class="detail-status" :class="'status-' + orderDetail.status">
            <span class="status-dot"></span>
            <span>{{ getStatusText(orderDetail.status) }}</span>
          </div>
          <div class="detail-order-no">
            <span class="label">订单号：</span>
            <span class="value">{{ orderDetail.orderNo }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><OfficeBuilding /></el-icon>
            房间信息
          </h4>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">房间号</span>
              <span class="info-value">{{ orderDetail.roomNumber }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">房型</span>
              <span class="info-value">{{ orderDetail.typeName }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><Calendar /></el-icon>
            入住信息
          </h4>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">入住日期</span>
              <span class="info-value highlight">{{ orderDetail.checkInDate }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">退房日期</span>
              <span class="info-value highlight">{{ orderDetail.checkOutDate }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">入住人数</span>
              <span class="info-value">{{ orderDetail.guestCount }}人</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><User /></el-icon>
            联系信息
          </h4>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">联系人</span>
              <span class="info-value">{{ orderDetail.contactName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">联系电话</span>
              <span class="info-value">{{ orderDetail.contactPhone }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><Document /></el-icon>
            其他信息
          </h4>
          <div class="section-content">
            <div class="info-row">
              <span class="info-label">特殊要求</span>
              <span class="info-value">{{ orderDetail.specialRequest || '无' }}</span>
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
        
        <div class="detail-total">
          <span class="total-label">订单金额</span>
          <span class="total-value">
            <span class="price-symbol">¥</span>
            <span class="price-amount">{{ orderDetail.totalAmount }}</span>
          </span>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog 
      v-model="payDialogVisible" 
      title="订单支付" 
      width="480px"
      class="pay-dialog"
      destroy-on-close
    >
      <div class="pay-content">
        <div class="pay-order-info">
          <div class="pay-row">
            <span class="pay-label">订单号</span>
            <span class="pay-value">{{ payOrderInfo.orderNo }}</span>
          </div>
          <div class="pay-row">
            <span class="pay-label">房间信息</span>
            <span class="pay-value">{{ payOrderInfo.roomNumber }} - {{ payOrderInfo.typeName }}</span>
          </div>
        </div>
        
        <div class="pay-amount-section">
          <span class="amount-label">支付金额</span>
          <div class="amount-value">
            <span class="price-symbol">¥</span>
            <span class="price-amount">{{ payOrderInfo.totalAmount }}</span>
          </div>
        </div>
        
        <el-form :model="payForm" class="pay-form">
          <el-form-item label="支付方式">
            <el-radio-group v-model="payForm.paymentMethod" class="payment-methods">
              <el-radio label="ALIPAY" class="payment-option">
                <div class="payment-content">
                  <el-icon class="payment-icon alipay"><Wallet /></el-icon>
                  <span>支付宝</span>
                </div>
              </el-radio>
              <el-radio label="WECHAT" class="payment-option">
                <div class="payment-content">
                  <el-icon class="payment-icon wechat"><ChatDotRound /></el-icon>
                  <span>微信支付</span>
                </div>
              </el-radio>
              <el-radio label="CASH" class="payment-option">
                <div class="payment-content">
                  <el-icon class="payment-icon cash"><Money /></el-icon>
                  <span>现金支付</span>
                </div>
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="payDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" :loading="payLoading" @click="submitPay" class="confirm-btn">
            <el-icon v-if="!payLoading"><Check /></el-icon>
            确认支付
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Calendar, User, Clock, Picture, View, Wallet, Close, 
  Document, OfficeBuilding, List, Check, ChatDotRound, Money 
} from '@element-plus/icons-vue'
import { orderApi } from '@/api/room'

const router = useRouter()

const loading = ref(false)
const payLoading = ref(false)
const detailVisible = ref(false)
const payDialogVisible = ref(false)
const orderList = ref([])
const orderDetail = ref({})
const payOrderInfo = ref({})
const total = ref(0)
const currentStatus = ref('')
const defaultImage = 'https://via.placeholder.com/200x150?text=Room'

const statusCounts = computed(() => {
  const counts = { pending: 0 }
  orderList.value.forEach(order => {
    if (order.status === 0) counts.pending++
  })
  return counts
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: ''
})

const payForm = reactive({
  paymentMethod: 'ALIPAY'
})

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

const getDaysCount = (checkIn, checkOut) => {
  if (!checkIn || !checkOut) return 0
  const start = new Date(checkIn)
  const end = new Date(checkOut)
  return Math.floor((end - start) / (1000 * 60 * 60 * 24))
}

const getList = async () => {
  loading.value = true
  try {
    const res = await orderApi.getList(queryParams)
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

const handleFilter = () => {
  queryParams.status = currentStatus.value
  queryParams.pageNum = 1
  getList()
}

const handleDetail = async (order) => {
  try {
    const res = await orderApi.getById(order.id)
    if (res.code === 200) {
      orderDetail.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
  }
}

const handlePay = (order) => {
  payOrderInfo.value = order
  payDialogVisible.value = true
}

const submitPay = async () => {
  payLoading.value = true
  try {
    const res = await orderApi.pay(payOrderInfo.value.id, {
      ...payForm,
      amount: payOrderInfo.value.totalAmount
    })
    if (res.code === 200) {
      ElMessage.success('支付成功')
      payDialogVisible.value = false
      getList()
    }
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    payLoading.value = false
  }
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await orderApi.cancel(order.id)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      getList()
    }
  } catch (error) {
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.my-orders {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  
  .page-header {
    margin-bottom: 24px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      
      .title-section {
        .page-title {
          font-size: 28px;
          font-weight: 700;
          color: $text-primary;
          margin: 0 0 8px;
          letter-spacing: -0.5px;
        }
        
        .page-subtitle {
          font-size: 14px;
          color: $text-secondary;
          margin: 0;
        }
      }
      
      .header-stats {
        display: flex;
        gap: 24px;
        
        .stat-item {
          text-align: center;
          padding: 16px 24px;
          background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
          border-radius: 12px;
          color: $white;
          
          .stat-value {
            display: block;
            font-size: 28px;
            font-weight: 700;
            line-height: 1;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 12px;
            opacity: 0.9;
          }
        }
      }
    }
  }
  
  .filter-tabs {
    margin-bottom: 24px;
    
    .status-filter {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      :deep(.el-radio-button) {
        .el-radio-button__inner {
          padding: 10px 20px;
          border-radius: 20px !important;
          border: 1px solid $border-color;
          background: $white;
          color: $text-regular;
          font-weight: 500;
          transition: all 0.3s ease;
          
          &:hover {
            border-color: $primary-light;
            color: $primary-light;
          }
        }
        
        &.is-active .el-radio-button__inner {
          background: $gradient-primary;
          border-color: transparent;
          color: $white;
          box-shadow: 0 4px 12px rgba($primary-color, 0.3);
        }
      }
    }
  }
  
  .order-list {
    .orders-container {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    
    .order-card {
      background: $white;
      border-radius: 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      overflow: hidden;
      transition: all 0.3s ease;
      border: 1px solid $border-light;
      
      &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
        transform: translateY(-2px);
      }
      
      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 24px;
        background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
        border-bottom: 1px solid $border-light;
        
        .order-info {
          display: flex;
          align-items: center;
          gap: 24px;
          
          .order-no-wrapper {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .order-no-label {
              font-size: 12px;
              color: $text-secondary;
              background: $white;
              padding: 4px 8px;
              border-radius: 4px;
            }
            
            .order-no-value {
              font-weight: 600;
              color: $text-primary;
              font-size: 14px;
            }
          }
          
          .order-time {
            display: flex;
            align-items: center;
            gap: 6px;
            color: $text-secondary;
            font-size: 13px;
            
            .el-icon {
              font-size: 14px;
            }
          }
        }
        
        .order-status {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 6px 16px;
          border-radius: 20px;
          font-size: 13px;
          font-weight: 600;
          
          .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            animation: pulse 2s infinite;
          }
          
          &.status-0 {
            background: rgba($warning-color, 0.1);
            color: $warning-dark;
            
            .status-dot { background: $warning-color; }
          }
          
          &.status-1 {
            background: rgba($warning-color, 0.1);
            color: $warning-dark;
            
            .status-dot { background: $warning-color; }
          }
          
          &.status-2, &.status-3 {
            background: rgba($primary-color, 0.1);
            color: $primary-color;
            
            .status-dot { background: $primary-light; }
          }
          
          &.status-4 {
            background: rgba($success-color, 0.1);
            color: $success-dark;
            
            .status-dot { background: $success-color; }
          }
          
          &.status-5 {
            background: rgba($text-secondary, 0.1);
            color: $text-secondary;
            
            .status-dot { background: $text-secondary; animation: none; }
          }
        }
      }
      
      .order-content {
        display: flex;
        justify-content: space-between;
        padding: 24px;
        gap: 24px;
        
        .room-info {
          display: flex;
          gap: 20px;
          flex: 1;
          
          .room-image {
            width: 160px;
            height: 110px;
            border-radius: 12px;
            overflow: hidden;
            position: relative;
            flex-shrink: 0;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
              transition: transform 0.3s ease;
            }
            
            .image-overlay {
              position: absolute;
              inset: 0;
              background: rgba(0, 0, 0, 0.3);
              display: flex;
              align-items: center;
              justify-content: center;
              opacity: 0;
              transition: opacity 0.3s ease;
              
              .el-icon {
                font-size: 32px;
                color: $white;
              }
            }
            
            &:hover {
              .image-overlay { opacity: 1; }
              img { transform: scale(1.05); }
            }
          }
          
          .room-detail {
            flex: 1;
            
            .room-title {
              font-size: 18px;
              font-weight: 600;
              color: $text-primary;
              margin: 0 0 16px;
            }
            
            .info-item {
              display: flex;
              align-items: center;
              gap: 10px;
              margin-bottom: 12px;
              
              .info-icon {
                color: $primary-light;
                font-size: 16px;
              }
              
              .info-text {
                font-size: 14px;
                color: $text-regular;
                display: flex;
                align-items: center;
                gap: 8px;
                
                .date-highlight {
                  font-weight: 600;
                  color: $text-primary;
                }
                
                .date-separator {
                  color: $text-secondary;
                }
                
                .days-badge {
                  background: rgba($primary-color, 0.1);
                  color: $primary-color;
                  padding: 2px 8px;
                  border-radius: 10px;
                  font-size: 12px;
                  font-weight: 600;
                }
              }
            }
          }
        }
        
        .order-price {
          text-align: right;
          display: flex;
          flex-direction: column;
          justify-content: center;
          padding-left: 24px;
          border-left: 1px solid $border-light;
          min-width: 140px;
          
          .price-label {
            font-size: 12px;
            color: $text-secondary;
            margin-bottom: 8px;
          }
          
          .price-value {
            display: flex;
            align-items: baseline;
            justify-content: flex-end;
            
            .price-symbol {
              font-size: 18px;
              font-weight: 600;
              color: $danger-color;
              margin-right: 2px;
            }
            
            .price-amount {
              font-size: 32px;
              font-weight: 700;
              color: $danger-color;
              line-height: 1;
            }
          }
        }
      }
      
      .order-footer {
        padding: 16px 24px;
        border-top: 1px solid $border-light;
        display: flex;
        justify-content: flex-end;
        background: #FAFBFC;
        
        .order-actions {
          display: flex;
          gap: 12px;
          
          .action-btn {
            display: flex;
            align-items: center;
            gap: 6px;
            padding: 10px 20px;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.3s ease;
            
            .el-icon {
              font-size: 16px;
            }
            
            &.detail-btn {
              background: $white;
              border: 1px solid $border-color;
              color: $text-regular;
              
              &:hover {
                border-color: $primary-light;
                color: $primary-light;
                background: rgba($primary-color, 0.05);
              }
            }
            
            &.pay-btn {
              background: $gradient-primary;
              border: none;
              color: $white;
              
              &:hover {
                box-shadow: 0 4px 12px rgba($primary-color, 0.4);
                transform: translateY(-1px);
              }
            }
            
            &.cancel-btn {
              background: $white;
              
              &:hover {
                background: rgba($danger-color, 0.1);
              }
            }
          }
        }
      }
    }
  }
  
  .empty-state {
    padding: 60px 20px;
    background: $white;
    border-radius: 16px;
    text-align: center;
    
    .empty-icon {
      color: $text-placeholder;
      margin-bottom: 16px;
    }
    
    .empty-content {
      h3 {
        font-size: 18px;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 8px;
      }
      
      p {
        font-size: 14px;
        color: $text-secondary;
        margin: 0 0 24px;
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding: 20px;
    background: $white;
    border-radius: 12px;
  }
}

.detail-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    
    .el-dialog__header {
      padding: 20px 24px;
      background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
      border-bottom: 1px solid $border-light;
    }
    
    .el-dialog__body {
      padding: 0;
    }
  }
  
  .detail-content {
    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 24px;
      background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
      color: $white;
      
      .detail-status {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 6px 16px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 20px;
        font-weight: 600;
        
        .status-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: $white;
        }
      }
      
      .detail-order-no {
        font-size: 14px;
        
        .label { opacity: 0.9; }
        .value { font-weight: 600; }
      }
    }
    
    .detail-section {
      padding: 20px 24px;
      border-bottom: 1px solid $border-light;
      
      &:last-of-type {
        border-bottom: none;
      }
      
      .section-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 15px;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 16px;
        
        .el-icon {
          color: $primary-light;
        }
      }
      
      .section-content {
        .info-row {
          display: flex;
          justify-content: space-between;
          padding: 10px 0;
          
          &:not(:last-child) {
            border-bottom: 1px dashed $border-light;
          }
          
          .info-label {
            font-size: 14px;
            color: $text-secondary;
          }
          
          .info-value {
            font-size: 14px;
            color: $text-primary;
            font-weight: 500;
            
            &.highlight {
              color: $primary-color;
              font-weight: 600;
            }
          }
        }
      }
    }
    
    .detail-total {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
      
      .total-label {
        font-size: 16px;
        font-weight: 600;
        color: $text-primary;
      }
      
      .total-value {
        display: flex;
        align-items: baseline;
        
        .price-symbol {
          font-size: 20px;
          font-weight: 600;
          color: $danger-color;
          margin-right: 2px;
        }
        
        .price-amount {
          font-size: 36px;
          font-weight: 700;
          color: $danger-color;
        }
      }
    }
  }
}

.pay-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    
    .el-dialog__header {
      padding: 20px 24px;
      background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
      border-bottom: 1px solid $border-light;
    }
    
    .el-dialog__body {
      padding: 24px;
    }
    
    .el-dialog__footer {
      padding: 16px 24px;
      border-top: 1px solid $border-light;
    }
  }
  
  .pay-content {
    .pay-order-info {
      background: #F8FAFC;
      border-radius: 12px;
      padding: 16px;
      margin-bottom: 20px;
      
      .pay-row {
        display: flex;
        justify-content: space-between;
        padding: 8px 0;
        
        &:not(:last-child) {
          border-bottom: 1px dashed $border-light;
        }
        
        .pay-label {
          font-size: 13px;
          color: $text-secondary;
        }
        
        .pay-value {
          font-size: 13px;
          color: $text-primary;
          font-weight: 500;
        }
      }
    }
    
    .pay-amount-section {
      text-align: center;
      padding: 24px;
      background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
      border-radius: 12px;
      margin-bottom: 24px;
      
      .amount-label {
        display: block;
        font-size: 13px;
        color: $text-secondary;
        margin-bottom: 8px;
      }
      
      .amount-value {
        display: flex;
        align-items: baseline;
        justify-content: center;
        
        .price-symbol {
          font-size: 24px;
          font-weight: 600;
          color: $danger-color;
          margin-right: 2px;
        }
        
        .price-amount {
          font-size: 42px;
          font-weight: 700;
          color: $danger-color;
        }
      }
    }
    
    .pay-form {
      .payment-methods {
        display: flex;
        flex-direction: column;
        gap: 12px;
        
        .payment-option {
          margin: 0;
          
          :deep(.el-radio__input) {
            &.is-checked {
              + .el-radio__label {
                .payment-content {
                  border-color: $primary-light;
                  background: rgba($primary-color, 0.05);
                }
              }
            }
          }
          
          :deep(.el-radio__label) {
            padding-left: 0;
          }
          
          .payment-content {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 16px;
            border: 1px solid $border-color;
            border-radius: 10px;
            transition: all 0.3s ease;
            width: 100%;
            
            .payment-icon {
              font-size: 24px;
              
              &.alipay { color: #1677FF; }
              &.wechat { color: #07C160; }
              &.cash { color: $warning-color; }
            }
            
            span {
              font-weight: 500;
              color: $text-primary;
            }
          }
        }
      }
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    
    .cancel-btn {
      padding: 10px 24px;
      border-radius: 8px;
    }
    
    .confirm-btn {
      padding: 10px 32px;
      border-radius: 8px;
      background: $gradient-primary;
      border: none;
      
      &:hover {
        box-shadow: 0 4px 12px rgba($primary-color, 0.4);
      }
    }
  }
}

.order-fade-enter-active,
.order-fade-leave-active {
  transition: all 0.3s ease;
}

.order-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.order-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@media (max-width: 768px) {
  .my-orders {
    padding: 16px;
    
    .page-header .header-content {
      flex-direction: column;
      gap: 16px;
      
      .header-stats {
        width: 100%;
        
        .stat-item {
          flex: 1;
        }
      }
    }
    
    .order-list .order-card {
      .order-content {
        flex-direction: column;
        
        .room-info {
          flex-direction: column;
          
          .room-image {
            width: 100%;
            height: 180px;
          }
        }
        
        .order-price {
          border-left: none;
          border-top: 1px solid $border-light;
          padding: 16px 0 0;
          margin-top: 16px;
          text-align: left;
          
          .price-value {
            justify-content: flex-start;
          }
        }
      }
      
      .order-footer .order-actions {
        flex-wrap: wrap;
        
        .action-btn {
          flex: 1;
          justify-content: center;
        }
      }
    }
  }
}
</style>
