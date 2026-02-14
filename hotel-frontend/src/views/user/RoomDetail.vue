<template>
  <div class="room-detail">
    <div class="detail-container" v-loading="loading">
      <div class="room-info-section">
        <div class="room-image">
          <img :src="roomDetail.imageUrl || defaultImage" :alt="roomDetail.typeName" />
          <div class="image-overlay">
            <el-tag :type="getStatusType(roomDetail.status)" size="large" effect="dark">
              {{ getStatusText(roomDetail.status) }}
            </el-tag>
          </div>
        </div>
        <div class="room-content">
          <div class="room-header">
            <h2 class="room-number">{{ roomDetail.roomNumber }}</h2>
            <span class="room-type-badge">{{ roomDetail.typeName }}</span>
          </div>
          
          <div class="room-meta">
            <div class="meta-item">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ roomDetail.typeName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Location /></el-icon>
              <span>{{ roomDetail.floor }}层</span>
            </div>
            <div class="meta-item">
              <el-icon><House /></el-icon>
              <span>{{ roomDetail.bedType || '标准床' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Grid /></el-icon>
              <span>{{ roomDetail.area || 25 }}㎡</span>
            </div>
          </div>
          
          <div class="room-price-section">
            <div class="price-label">每晚价格</div>
            <div class="room-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ roomDetail.price }}</span>
            </div>
          </div>
          
          <div class="room-description">
            <h4>房间描述</h4>
            <p>{{ roomDetail.description || '暂无描述' }}</p>
          </div>
          
          <div class="room-facilities">
            <h4>房间设施</h4>
            <div class="facilities-list">
              <el-tag v-for="(facility, index) in getFacilities(roomDetail.facilities)" :key="index" effect="plain">
                {{ facility }}
              </el-tag>
              <span v-if="!roomDetail.facilities" class="no-data">暂无设施信息</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="booking-section" v-if="roomDetail.status === 0">
        <h3>预订信息</h3>
        <el-form ref="formRef" :model="bookingForm" :rules="bookingRules" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入住日期" prop="checkInDate">
                <el-date-picker
                  v-model="bookingForm.checkInDate"
                  type="date"
                  placeholder="选择入住日期"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledDate"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="退房日期" prop="checkOutDate">
                <el-date-picker
                  v-model="bookingForm.checkOutDate"
                  type="date"
                  placeholder="选择退房日期"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledDate"
                  style="width: 100%"
                  @change="calculateTotal"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入住人数" prop="guestCount">
                <el-input-number v-model="bookingForm.guestCount" :min="1" :max="10" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="bookingForm.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="联系人" prop="contactName">
            <el-input v-model="bookingForm.contactName" placeholder="请输入联系人姓名" />
          </el-form-item>
          <el-form-item label="特殊要求" prop="specialRequest">
            <el-input v-model="bookingForm.specialRequest" type="textarea" :rows="3" placeholder="请输入特殊要求（选填）" />
          </el-form-item>
          
          <div class="booking-summary">
            <div class="summary-item">
              <span class="label">入住天数</span>
              <span class="value">{{ daysCount }}晚</span>
            </div>
            <div class="summary-item total">
              <span class="label">订单金额</span>
              <span class="value">¥{{ totalAmount }}</span>
            </div>
          </div>
          
          <el-form-item>
            <el-button type="primary" size="large" :loading="submitLoading" @click="handleSubmit">
              提交预订
            </el-button>
            <el-button size="large" @click="router.back()">返回</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="booking-section not-available" v-else>
        <el-result
          icon="warning"
          title="该客房暂不可预订"
          sub-title="请选择其他客房或稍后再试"
        >
          <template #extra>
            <el-button type="primary" @click="router.push('/user/rooms')">查看其他客房</el-button>
          </template>
        </el-result>
      </div>
    </div>
    
    <el-dialog v-model="payDialogVisible" title="订单支付" width="420px" class="pay-dialog">
      <div class="pay-info">
        <div class="pay-item">
          <span class="label">订单号</span>
          <span class="value">{{ orderInfo.orderNo }}</span>
        </div>
        <div class="pay-item total">
          <span class="label">支付金额</span>
          <span class="value price">¥{{ orderInfo.totalAmount }}</span>
        </div>
      </div>
      <el-form :model="payForm" label-width="80px" class="pay-form">
        <el-form-item label="支付方式">
          <el-radio-group v-model="payForm.paymentMethod">
            <el-radio label="ALIPAY">支付宝</el-radio>
            <el-radio label="WECHAT">微信支付</el-radio>
            <el-radio label="CASH">现金支付</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="payLoading" @click="handlePay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, Location, House, Grid } from '@element-plus/icons-vue'
import { roomApi, orderApi } from '@/api/room'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const submitLoading = ref(false)
const payLoading = ref(false)
const payDialogVisible = ref(false)
const formRef = ref(null)
const roomDetail = ref({})
const orderInfo = ref({})
const defaultImage = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600&h=400&fit=crop'

const bookingForm = reactive({
  checkInDate: '',
  checkOutDate: '',
  guestCount: 1,
  contactName: userStore.realName || '',
  contactPhone: '',
  specialRequest: ''
})

const payForm = reactive({
  paymentMethod: 'ALIPAY'
})

const bookingRules = {
  checkInDate: [
    { required: true, message: '请选择入住日期', trigger: 'change' }
  ],
  checkOutDate: [
    { required: true, message: '请选择退房日期', trigger: 'change' }
  ],
  guestCount: [
    { required: true, message: '请选择入住人数', trigger: 'change' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const statusMap = {
  0: { text: '空闲', type: 'success' },
  1: { text: '已预订', type: 'warning' },
  2: { text: '已入住', type: 'primary' },
  3: { text: '维护中', type: 'info' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

const daysCount = computed(() => {
  if (bookingForm.checkInDate && bookingForm.checkOutDate) {
    const start = new Date(bookingForm.checkInDate)
    const end = new Date(bookingForm.checkOutDate)
    const diff = Math.floor((end - start) / (1000 * 60 * 60 * 24))
    return diff > 0 ? diff : 0
  }
  return 0
})

const totalAmount = computed(() => {
  return (daysCount.value * (roomDetail.value.price || 0)).toFixed(2)
})

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

const getFacilities = (facilities) => {
  if (!facilities) return []
  return facilities.split(',')
}

const getDetail = async () => {
  loading.value = true
  try {
    const res = await roomApi.getById(route.params.id)
    if (res.code === 200) {
      roomDetail.value = res.data
      
      if (route.query.action === 'book') {
        bookingForm.checkInDate = route.query.checkInDate || ''
        bookingForm.checkOutDate = route.query.checkOutDate || ''
      }
    }
  } catch (error) {
    console.error('获取客房详情失败:', error)
  } finally {
    loading.value = false
  }
}

const calculateTotal = () => {}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (bookingForm.checkInDate >= bookingForm.checkOutDate) {
    ElMessage.warning('退房日期必须晚于入住日期')
    return
  }
  
  submitLoading.value = true
  try {
    const res = await orderApi.create({
      roomId: roomDetail.value.id,
      checkInDate: bookingForm.checkInDate,
      checkOutDate: bookingForm.checkOutDate,
      guestCount: bookingForm.guestCount,
      contactName: bookingForm.contactName,
      contactPhone: bookingForm.contactPhone,
      specialRequest: bookingForm.specialRequest,
      totalAmount: totalAmount.value
    })
    if (res.code === 200) {
      orderInfo.value = res.data
      payDialogVisible.value = true
    }
  } catch (error) {
    console.error('预订失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handlePay = async () => {
  payLoading.value = true
  try {
    const res = await orderApi.pay(orderInfo.value.id, {
      ...payForm,
      amount: orderInfo.value.totalAmount
    })
    if (res.code === 200) {
      ElMessage.success('支付成功')
      payDialogVisible.value = false
      router.push('/user/orders')
    }
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    payLoading.value = false
  }
}

onMounted(() => {
  getDetail()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.room-detail {
  max-width: 1200px;
  margin: 0 auto;
  
  .detail-container {
    background: $white;
    border-radius: $border-radius-xl;
    overflow: hidden;
    box-shadow: $shadow-sm;
    
    .room-info-section {
      display: flex;
      gap: $spacing-2xl;
      padding: $spacing-2xl;
      
      @media (max-width: 992px) {
        flex-direction: column;
      }
      
      .room-image {
        width: 500px;
        flex-shrink: 0;
        position: relative;
        border-radius: $border-radius-lg;
        overflow: hidden;
        
        @media (max-width: 992px) {
          width: 100%;
        }
        
        img {
          width: 100%;
          height: 350px;
          object-fit: cover;
        }
        
        .image-overlay {
          position: absolute;
          top: $spacing-md;
          left: $spacing-md;
        }
      }
      
      .room-content {
        flex: 1;
        
        .room-header {
          display: flex;
          align-items: center;
          gap: $spacing-md;
          margin-bottom: $spacing-lg;
          
          .room-number {
            font-size: $font-size-3xl;
            font-weight: $font-weight-bold;
            color: $text-primary;
            margin: 0;
          }
          
          .room-type-badge {
            background: $gradient-primary;
            color: $white;
            padding: $spacing-xs $spacing-md;
            border-radius: $border-radius-full;
            font-size: $font-size-sm;
            font-weight: $font-weight-medium;
          }
        }
        
        .room-meta {
          display: flex;
          flex-wrap: wrap;
          gap: $spacing-xl;
          margin-bottom: $spacing-xl;
          
          .meta-item {
            display: flex;
            align-items: center;
            gap: $spacing-sm;
            font-size: $font-size-base;
            color: $text-secondary;
            
            .el-icon {
              font-size: $font-size-lg;
              color: $primary-color;
            }
          }
        }
        
        .room-price-section {
          background: linear-gradient(135deg, rgba($danger-color, 0.05) 0%, rgba($danger-color, 0.02) 100%);
          border-radius: $border-radius-lg;
          padding: $spacing-lg;
          margin-bottom: $spacing-xl;
          
          .price-label {
            font-size: $font-size-sm;
            color: $text-secondary;
            margin-bottom: $spacing-xs;
          }
          
          .room-price {
            display: flex;
            align-items: baseline;
            
            .price-symbol {
              font-size: $font-size-lg;
              color: $danger-color;
              font-weight: $font-weight-medium;
            }
            
            .price-value {
              font-size: $font-size-4xl;
              color: $danger-color;
              font-weight: $font-weight-bold;
              line-height: 1;
            }
          }
        }
        
        .room-description, .room-facilities {
          margin-bottom: $spacing-xl;
          
          h4 {
            font-size: $font-size-md;
            font-weight: $font-weight-semibold;
            color: $text-primary;
            margin-bottom: $spacing-md;
            padding-left: $spacing-sm;
            border-left: 3px solid $primary-color;
          }
          
          p {
            font-size: $font-size-base;
            color: $text-regular;
            line-height: $line-height-relaxed;
          }
          
          .facilities-list {
            display: flex;
            flex-wrap: wrap;
            gap: $spacing-sm;
            
            .no-data {
              color: $text-placeholder;
              font-size: $font-size-sm;
            }
          }
        }
      }
    }
    
    .booking-section {
      padding: $spacing-2xl;
      border-top: 1px solid $border-light;
      background: $bg-color;
      
      h3 {
        font-size: $font-size-lg;
        font-weight: $font-weight-semibold;
        color: $text-primary;
        margin-bottom: $spacing-xl;
        padding-left: $spacing-sm;
        border-left: 3px solid $primary-color;
      }
      
      .booking-summary {
        background: $white;
        border-radius: $border-radius-lg;
        padding: $spacing-lg;
        margin-bottom: $spacing-xl;
        border: 1px solid $border-light;
        
        .summary-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: $spacing-sm 0;
          
          .label {
            font-size: $font-size-base;
            color: $text-secondary;
          }
          
          .value {
            font-size: $font-size-md;
            font-weight: $font-weight-medium;
            color: $text-primary;
          }
          
          &.total {
            padding-top: $spacing-md;
            margin-top: $spacing-sm;
            border-top: 1px solid $border-light;
            
            .value {
              font-size: $font-size-2xl;
              font-weight: $font-weight-bold;
              color: $danger-color;
            }
          }
        }
      }
      
      &.not-available {
        text-align: center;
        padding: $spacing-4xl $spacing-xl;
      }
    }
  }
  
  .pay-dialog {
    .pay-info {
      background: $bg-color;
      border-radius: $border-radius-lg;
      padding: $spacing-lg;
      margin-bottom: $spacing-lg;
      
      .pay-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: $spacing-sm 0;
        
        .label {
          color: $text-secondary;
        }
        
        .value {
          font-weight: $font-weight-medium;
          color: $text-primary;
          
          &.price {
            font-size: $font-size-xl;
            font-weight: $font-weight-bold;
            color: $danger-color;
          }
        }
        
        &.total {
          border-top: 1px solid $border-light;
          margin-top: $spacing-sm;
          padding-top: $spacing-md;
        }
      }
    }
  }
}
</style>
