<template>
  <div class="room-list">
    <div class="page-header">
      <h2 class="page-title">客房预订</h2>
    </div>
    
    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="入住日期">
          <el-date-picker
            v-model="searchForm.checkInDate"
            type="date"
            placeholder="选择入住日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="退房日期">
          <el-date-picker
            v-model="searchForm.checkOutDate"
            type="date"
            placeholder="选择退房日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="searchForm.typeId" placeholder="全部房型" clearable style="width: 140px">
            <el-option
              v-for="item in roomTypes"
              :key="item.id"
              :label="item.typeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格范围">
          <el-input-number v-model="searchForm.minPrice" :min="0" placeholder="最低价" style="width: 120px" />
          <span class="price-separator">-</span>
          <el-input-number v-model="searchForm.maxPrice" :min="0" placeholder="最高价" style="width: 120px" />
        </el-form-item>
        <el-form-item>
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
    
    <div class="room-grid" v-loading="loading">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="room in roomList" :key="room.id">
          <div class="room-card hover-lift" @click="handleDetail(room)">
            <div class="room-image">
              <img :src="room.imageUrl || defaultImage" :alt="room.typeName" />
              <div class="status-badge" :class="getStatusClass(room.status)">
                {{ getStatusText(room.status) }}
              </div>
            </div>
            <div class="room-info">
              <div class="room-header">
                <h3 class="room-number">{{ room.roomNumber }}</h3>
                <span class="room-type">{{ room.typeName }}</span>
              </div>
              <div class="room-details">
                <span class="detail-item">
                  <el-icon><Location /></el-icon>
                  {{ room.floor }}层
                </span>
                <span class="detail-item">
                  <el-icon><House /></el-icon>
                  {{ room.bedType || '标准床' }}
                </span>
              </div>
              <div class="room-facilities">
                <el-tag v-for="(facility, index) in getFacilities(room.facilities)" :key="index" size="small" effect="plain">
                  {{ facility }}
                </el-tag>
              </div>
              <div class="room-footer">
                <div class="room-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ room.price }}</span>
                  <span class="price-unit">/晚</span>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  :disabled="room.status !== 0"
                  @click.stop="handleBook(room)"
                >
                  立即预订
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <el-empty v-if="!loading && roomList.length === 0" description="暂无可预订客房" />
      
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[9, 18, 36]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Location, House } from '@element-plus/icons-vue'
import { roomApi } from '@/api/room'

const router = useRouter()

const loading = ref(false)
const roomList = ref([])
const roomTypes = ref([])
const total = ref(0)
const defaultImage = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400&h=250&fit=crop'

const queryParams = reactive({
  pageNum: 1,
  pageSize: 9
})

const searchForm = reactive({
  checkInDate: '',
  checkOutDate: '',
  typeId: '',
  minPrice: null,
  maxPrice: null
})

const statusMap = {
  0: { text: '空闲', class: 'available' },
  1: { text: '已预订', class: 'booked' },
  2: { text: '已入住', class: 'occupied' },
  3: { text: '维护中', class: 'maintenance' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusClass = (status) => statusMap[status]?.class || ''

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

const getFacilities = (facilities) => {
  if (!facilities) return []
  return facilities.split(',').slice(0, 3)
}

const getRoomTypes = async () => {
  try {
    const res = await roomApi.getTypes()
    if (res.code === 200) {
      roomTypes.value = res.data
    }
  } catch (error) {
    console.error('获取房型失败:', error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await roomApi.getList(queryParams)
    if (res.code === 200) {
      roomList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取客房列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (searchForm.checkInDate && searchForm.checkOutDate) {
    if (searchForm.checkInDate >= searchForm.checkOutDate) {
      ElMessage.warning('退房日期必须晚于入住日期')
      return
    }
  }
  
  loading.value = true
  try {
    const res = await roomApi.search({
      ...searchForm,
      ...queryParams
    })
    if (res.code === 200) {
      roomList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.checkInDate = ''
  searchForm.checkOutDate = ''
  searchForm.typeId = ''
  searchForm.minPrice = null
  searchForm.maxPrice = null
  queryParams.pageNum = 1
  getList()
}

const handleDetail = (room) => {
  router.push(`/user/room/${room.id}`)
}

const handleBook = (room) => {
  router.push({
    path: `/user/room/${room.id}`,
    query: {
      action: 'book',
      checkInDate: searchForm.checkInDate,
      checkOutDate: searchForm.checkOutDate
    }
  })
}

onMounted(() => {
  getRoomTypes()
  getList()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.room-list {
  max-width: 1280px;
  margin: 0 auto;
  
  .page-header {
    margin-bottom: $spacing-lg;
    
    .page-title {
      font-size: $font-size-xl;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      position: relative;
      padding-left: $spacing-md;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 20px;
        background: $gradient-primary;
        border-radius: $border-radius-full;
      }
    }
  }
  
  .search-form {
    background: $white;
    border-radius: $border-radius-lg;
    padding: $spacing-lg;
    margin-bottom: $spacing-lg;
    box-shadow: $shadow-sm;
    
    .price-separator {
      margin: 0 $spacing-sm;
      color: $text-secondary;
    }
  }
  
  .room-grid {
    .room-card {
      background: $white;
      border-radius: $border-radius-lg;
      overflow: hidden;
      box-shadow: $shadow-sm;
      cursor: pointer;
      transition: all $transition-base;
      margin-bottom: $spacing-lg;
      border: 1px solid transparent;
      
      &:hover {
        border-color: rgba($primary-color, 0.2);
        box-shadow: $shadow-md;
      }
      
      .room-image {
        position: relative;
        height: 180px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform $transition-slow;
        }
        
        &:hover img {
          transform: scale(1.05);
        }
        
        .status-badge {
          position: absolute;
          top: $spacing-md;
          right: $spacing-md;
          padding: $spacing-xs $spacing-sm;
          border-radius: $border-radius-full;
          font-size: $font-size-xs;
          font-weight: $font-weight-medium;
          
          &.available {
            background: rgba($success-color, 0.9);
            color: $white;
          }
          
          &.booked {
            background: rgba($warning-color, 0.9);
            color: $white;
          }
          
          &.occupied {
            background: rgba($primary-color, 0.9);
            color: $white;
          }
          
          &.maintenance {
            background: rgba($text-secondary, 0.9);
            color: $white;
          }
        }
      }
      
      .room-info {
        padding: $spacing-lg;
        
        .room-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: $spacing-sm;
          
          .room-number {
            font-size: $font-size-lg;
            font-weight: $font-weight-semibold;
            color: $text-primary;
            margin: 0;
          }
          
          .room-type {
            font-size: $font-size-sm;
            color: $text-secondary;
            background: $bg-color;
            padding: $spacing-xs $spacing-sm;
            border-radius: $border-radius-base;
          }
        }
        
        .room-details {
          display: flex;
          gap: $spacing-lg;
          margin-bottom: $spacing-md;
          
          .detail-item {
            display: flex;
            align-items: center;
            gap: $spacing-xs;
            font-size: $font-size-sm;
            color: $text-secondary;
          }
        }
        
        .room-facilities {
          display: flex;
          flex-wrap: wrap;
          gap: $spacing-xs;
          margin-bottom: $spacing-md;
          
          .el-tag {
            border-radius: $border-radius-base;
          }
        }
        
        .room-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: $spacing-md;
          border-top: 1px solid $border-light;
          
          .room-price {
            display: flex;
            align-items: baseline;
            
            .price-symbol {
              font-size: $font-size-base;
              color: $danger-color;
              font-weight: $font-weight-medium;
            }
            
            .price-value {
              font-size: $font-size-2xl;
              color: $danger-color;
              font-weight: $font-weight-bold;
              line-height: 1;
            }
            
            .price-unit {
              font-size: $font-size-sm;
              color: $text-secondary;
              margin-left: 2px;
            }
          }
        }
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: $spacing-lg;
    padding: $spacing-lg;
    background: $white;
    border-radius: $border-radius-lg;
  }
}
</style>
