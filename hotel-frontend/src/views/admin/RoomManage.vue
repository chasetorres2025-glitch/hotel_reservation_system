<template>
  <div class="room-manage">
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-icon">
            <el-icon :size="28"><OfficeBuilding /></el-icon>
          </div>
          <div class="header-text">
            <h2 class="page-title">客房管理</h2>
            <p class="page-desc">管理酒店所有客房信息、状态和配置</p>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleAdd" class="add-btn">
            <el-icon><Plus /></el-icon>
            新增客房
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="stat-cards">
      <div class="stat-card total">
        <div class="card-icon">
          <el-icon :size="24"><HomeFilled /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ roomStats.total }}</div>
          <div class="card-label">总客房数</div>
        </div>
      </div>
      <div class="stat-card available">
        <div class="card-icon">
          <el-icon :size="24"><CircleCheck /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ roomStats.available }}</div>
          <div class="card-label">空闲客房</div>
        </div>
      </div>
      <div class="stat-card booked">
        <div class="card-icon">
          <el-icon :size="24"><Calendar /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ roomStats.booked }}</div>
          <div class="card-label">已预订</div>
        </div>
      </div>
      <div class="stat-card occupied">
        <div class="card-icon">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ roomStats.occupied }}</div>
          <div class="card-label">已入住</div>
        </div>
      </div>
      <div class="stat-card maintenance">
        <div class="card-icon">
          <el-icon :size="24"><Tools /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ roomStats.maintenance }}</div>
          <div class="card-label">维护中</div>
        </div>
      </div>
    </div>
    
    <div class="search-form">
      <div class="search-header">
        <el-icon class="search-icon"><Search /></el-icon>
        <span class="search-title">筛选条件</span>
      </div>
      <el-form :model="queryParams" inline class="search-content">
        <el-form-item label="房间号">
          <el-input v-model="queryParams.roomNumber" placeholder="请输入房间号" clearable prefix-icon="Search" />
        </el-form-item>
        <el-form-item label="客房类型">
          <el-select v-model="queryParams.typeId" placeholder="请选择类型" clearable>
            <el-option
              v-for="item in roomTypes"
              :key="item.id"
              :label="item.typeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="空闲" :value="0" />
            <el-option label="预订" :value="1" />
            <el-option label="入住" :value="2" />
            <el-option label="维护" :value="3" />
          </el-select>
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
    
    <!-- 客房列表 -->
    <div class="table-container">
      <div class="table-header">
        <span class="table-title">客房列表</span>
        <span class="table-count">共 {{ total }} 条记录</span>
      </div>
      <el-table :data="roomList" v-loading="loading" class="custom-table">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="roomNumber" label="房间号" width="120">
          <template #default="{ row }">
            <div class="room-number">
              <el-icon class="room-icon"><Key /></el-icon>
              <span>{{ row.roomNumber }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="客房类型" width="140">
          <template #default="{ row }">
            <div class="type-tag">
              <el-icon><HomeFilled /></el-icon>
              <span>{{ row.typeName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="floor" label="楼层" width="80" align="center">
          <template #default="{ row }">
            <span class="floor-badge">{{ row.floor }}F</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="130">
          <template #default="{ row }">
            <div class="price-cell">
              <span class="price">¥{{ row.price }}</span>
              <span class="price-unit">/晚</span>
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
        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="{ row }">
            <span class="description-text">{{ row.description || '暂无描述' }}</span>
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleEdit(row)" class="action-btn edit">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="warning" size="small" @click="handleStatus(row)" class="action-btn status">
                <el-icon><Setting /></el-icon>
                状态
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)" class="action-btn delete">
                <el-icon><Delete /></el-icon>
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="640px"
      @close="resetForm"
      class="custom-dialog"
    >
      <div class="dialog-content">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="room-form">
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="房间号" prop="roomNumber">
                <el-input v-model="form.roomNumber" placeholder="请输入房间号" prefix-icon="Key" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="客房类型" prop="typeId">
                <el-select v-model="form.typeId" placeholder="请选择类型" @change="handleTypeChange" style="width: 100%">
                  <el-option
                    v-for="item in roomTypes"
                    :key="item.id"
                    :label="item.typeName"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="楼层" prop="floor">
                <el-input-number v-model="form.floor" :min="1" :max="99" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="价格" prop="price">
                <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status" class="status-radio-group">
              <el-radio :label="0" class="status-radio available">
                <span class="radio-dot"></span>
                空闲
              </el-radio>
              <el-radio :label="1" class="status-radio booked">
                <span class="radio-dot"></span>
                预订
              </el-radio>
              <el-radio :label="2" class="status-radio occupied">
                <span class="radio-dot"></span>
                入住
              </el-radio>
              <el-radio :label="3" class="status-radio maintenance">
                <span class="radio-dot"></span>
                维护
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入客房描述" />
          </el-form-item>
          <el-form-item label="图片URL" prop="imageUrl">
            <el-input v-model="form.imageUrl" placeholder="请输入图片URL" prefix-icon="Picture" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading" class="submit-btn">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <el-dialog v-model="statusDialogVisible" title="修改客房状态" width="420px" class="status-dialog">
      <div class="status-dialog-content">
        <div class="current-status">
          <span class="label">房间号：</span>
          <span class="value room-num">{{ statusForm.roomNumber }}</span>
        </div>
        <div class="current-status">
          <span class="label">当前状态：</span>
          <div class="status-badge" :class="getStatusClass(statusForm.currentStatus)">
            <span class="status-dot"></span>
            <span>{{ getStatusText(statusForm.currentStatus) }}</span>
          </div>
        </div>
        <div class="status-select">
          <span class="label">新状态：</span>
          <el-radio-group v-model="statusForm.newStatus" class="status-radio-group">
            <el-radio :label="0">空闲</el-radio>
            <el-radio :label="1">预订</el-radio>
            <el-radio :label="2">入住</el-radio>
            <el-radio :label="3">维护</el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatus" :loading="statusLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Search, Refresh, Edit, Delete, Setting, 
  OfficeBuilding, HomeFilled, CircleCheck, Calendar, User, Tools,
  Key, Clock, Picture
} from '@element-plus/icons-vue'
import { adminRoomApi } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const statusLoading = ref(false)
const roomList = ref([])
const roomTypes = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const formRef = ref(null)

const roomStats = computed(() => {
  const stats = { total: 0, available: 0, booked: 0, occupied: 0, maintenance: 0 }
  roomList.value.forEach(room => {
    stats.total++
    if (room.status === 0) stats.available++
    else if (room.status === 1) stats.booked++
    else if (room.status === 2) stats.occupied++
    else if (room.status === 3) stats.maintenance++
  })
  return stats
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roomNumber: '',
  typeId: '',
  status: ''
})

const form = reactive({
  id: null,
  roomNumber: '',
  typeId: '',
  floor: 1,
  price: 0,
  status: 0,
  description: '',
  imageUrl: ''
})

const statusForm = reactive({
  id: null,
  roomNumber: '',
  currentStatus: 0,
  newStatus: 0
})

const isEdit = computed(() => !!form.id)
const dialogTitle = computed(() => isEdit.value ? '编辑客房' : '新增客房')

// 状态映射
const statusMap = {
  0: { text: '空闲', type: 'success' },
  1: { text: '预订', type: 'warning' },
  2: { text: '入住', type: 'primary' },
  3: { text: '维护', type: 'info' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'
const getStatusClass = (status) => {
  const classes = {
    0: 'available',
    1: 'booked',
    2: 'occupied',
    3: 'maintenance'
  }
  return classes[status] || 'available'
}

// 表单验证规则
const rules = {
  roomNumber: [
    { required: true, message: '请输入房间号', trigger: 'blur' }
  ],
  typeId: [
    { required: true, message: '请选择客房类型', trigger: 'change' }
  ],
  floor: [
    { required: true, message: '请输入楼层', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ]
}

// 方法
const getRoomTypes = async () => {
  try {
    const res = await adminRoomApi.getTypeList()
    if (res.code === 200) {
      roomTypes.value = res.data
    }
  } catch (error) {
    console.error('获取客房类型失败:', error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await adminRoomApi.getList(queryParams)
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

const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

const handleReset = () => {
  queryParams.roomNumber = ''
  queryParams.typeId = ''
  queryParams.status = ''
  handleSearch()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该客房吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await adminRoomApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getList()
    }
  } catch (error) {
    // 用户取消
  }
}

const handleStatus = (row) => {
  statusForm.id = row.id
  statusForm.roomNumber = row.roomNumber
  statusForm.currentStatus = row.status
  statusForm.newStatus = row.status
  statusDialogVisible.value = true
}

const submitStatus = async () => {
  statusLoading.value = true
  try {
    const res = await adminRoomApi.updateStatus({
      id: statusForm.id,
      status: statusForm.newStatus
    })
    if (res.code === 200) {
      ElMessage.success('状态修改成功')
      statusDialogVisible.value = false
      getList()
    }
  } catch (error) {
    console.error('状态修改失败:', error)
  } finally {
    statusLoading.value = false
  }
}

const handleTypeChange = (typeId) => {
  const type = roomTypes.value.find(t => t.id === typeId)
  if (type) {
    form.price = type.basePrice
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    const api = isEdit.value ? adminRoomApi.update : adminRoomApi.add
    const res = await api(form)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      getList()
    }
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.roomNumber = ''
  form.typeId = ''
  form.floor = 1
  form.price = 0
  form.status = 0
  form.description = ''
  form.imageUrl = ''
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  getRoomTypes()
  getList()
})
</script>

<style lang="scss" scoped>
.room-manage {
  .page-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 24px 32px;
    margin-bottom: 24px;
    box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
    
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
      .add-btn {
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
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      
      &.available .card-icon {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      }
      
      &.booked .card-icon {
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
      }
      
      &.occupied .card-icon {
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      }
      
      &.maintenance .card-icon {
        background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
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
        color: #667eea;
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
      
      .room-number {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .room-icon {
          color: #667eea;
        }
        
        span {
          font-weight: 600;
          color: #1f2937;
        }
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
          color: #667eea;
        }
      }
      
      .floor-badge {
        display: inline-block;
        padding: 4px 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 6px;
        font-size: 13px;
        font-weight: 600;
      }
      
      .price-cell {
        display: flex;
        align-items: baseline;
        gap: 2px;
        
        .price {
          font-size: 16px;
          font-weight: 700;
          color: #ef4444;
        }
        
        .price-unit {
          font-size: 12px;
          color: #9ca3af;
        }
      }
      
      .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 500;
        
        .status-dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
        }
        
        &.available {
          background: #d1fae5;
          color: #065f46;
          
          .status-dot {
            background: #10b981;
          }
        }
        
        &.booked {
          background: #fef3c7;
          color: #92400e;
          
          .status-dot {
            background: #f59e0b;
          }
        }
        
        &.occupied {
          background: #dbeafe;
          color: #1e40af;
          
          .status-dot {
            background: #3b82f6;
          }
        }
        
        &.maintenance {
          background: #f3f4f6;
          color: #374151;
          
          .status-dot {
            background: #6b7280;
          }
        }
      }
      
      .description-text {
        color: #6b7280;
        font-size: 13px;
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
        
        .action-btn {
          padding: 6px 12px;
          border-radius: 6px;
          font-size: 12px;
          font-weight: 500;
          transition: all 0.2s ease;
          
          &.edit {
            background: #eff6ff;
            border-color: #3b82f6;
            color: #3b82f6;
            
            &:hover {
              background: #3b82f6;
              color: white;
            }
          }
          
          &.status {
            background: #fef3c7;
            border-color: #f59e0b;
            color: #f59e0b;
            
            &:hover {
              background: #f59e0b;
              color: white;
            }
          }
          
          &.delete {
            background: #fee2e2;
            border-color: #ef4444;
            color: #ef4444;
            
            &:hover {
              background: #ef4444;
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
  
  .custom-dialog {
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
    
    .dialog-content {
      .room-form {
        :deep(.el-form-item__label) {
          font-weight: 500;
          color: #374151;
        }
        
        :deep(.el-input__wrapper),
        :deep(.el-select__wrapper) {
          border-radius: 8px;
        }
        
        .status-radio-group {
          display: flex;
          gap: 12px;
          flex-wrap: wrap;
          
          .status-radio {
            margin-right: 0;
            
            :deep(.el-radio__label) {
              display: flex;
              align-items: center;
              gap: 6px;
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
        padding: 10px 20px;
        border-radius: 8px;
      }
      
      .submit-btn {
        padding: 10px 24px;
        border-radius: 8px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        
        &:hover {
          opacity: 0.9;
        }
      }
    }
  }
  
  .status-dialog {
    .status-dialog-content {
      .current-status {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 16px;
        
        .label {
          font-size: 14px;
          color: #6b7280;
          min-width: 80px;
        }
        
        .value {
          font-weight: 500;
          color: #1f2937;
          
          &.room-num {
            font-weight: 600;
            color: #667eea;
          }
        }
      }
      
      .status-select {
        display: flex;
        align-items: center;
        gap: 12px;
        padding-top: 16px;
        border-top: 1px solid #f3f4f6;
        
        .label {
          font-size: 14px;
          color: #6b7280;
          min-width: 80px;
        }
        
        .status-radio-group {
          display: flex;
          gap: 12px;
          flex-wrap: wrap;
        }
      }
    }
  }
}
</style>
