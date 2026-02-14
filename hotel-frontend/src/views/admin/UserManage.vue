<template>
  <div class="user-manage">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2 class="page-title">用户管理</h2>
          <p class="page-subtitle">管理系统用户账户与权限</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleAdd" class="add-btn">
            <el-icon><Plus /></el-icon>
            <span>新增用户</span>
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="search-section">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item>
          <el-input v-model="queryParams.username" placeholder="搜索用户名" clearable class="search-input">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.role" placeholder="角色筛选" clearable class="filter-select">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.status" placeholder="状态筛选" clearable class="filter-select">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset" class="reset-btn">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="table-container">
      <el-table :data="userList" v-loading="loading" class="data-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="140">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="36" class="user-avatar">
                {{ row.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <div class="role-tag" :class="row.role">
              <el-icon v-if="row.role === 'admin'"><Star /></el-icon>
              <el-icon v-else><User /></el-icon>
              <span>{{ row.role === 'admin' ? '管理员' : '用户' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
              class="status-switch"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" link @click="handleEdit(row)" class="action-btn edit">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="warning" link @click="handleResetPwd(row)" class="action-btn reset">
                <el-icon><Key /></el-icon>
                重置
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)" class="action-btn delete">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
          background
        />
      </div>
    </div>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="540px"
      class="form-dialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="!isEdit">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password>
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名">
                <template #prefix>
                  <el-icon><Avatar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号">
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="form.role" placeholder="请选择角色" class="full-width">
                <el-option label="管理员" value="admin">
                  <div class="select-option">
                    <el-icon><Star /></el-icon>
                    <span>管理员</span>
                  </div>
                </el-option>
                <el-option label="普通用户" value="user">
                  <div class="select-option">
                    <el-icon><User /></el-icon>
                    <span>普通用户</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status" class="status-radio">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading" class="submit-btn">
            <el-icon v-if="!submitLoading"><Check /></el-icon>
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="420px" class="form-dialog" destroy-on-close>
      <el-form ref="resetPwdRef" :model="resetPwdForm" :rules="resetPwdRules" label-position="top" class="dialog-form">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdForm.password" type="password" placeholder="请输入新密码" show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetPwdForm.confirmPassword" type="password" placeholder="请确认新密码" show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetPwdVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="submitResetPwd" :loading="resetPwdLoading" class="submit-btn">
            <el-icon v-if="!resetPwdLoading"><Check /></el-icon>
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Key, User, Star, Lock, Check, Phone, Message, Avatar } from '@element-plus/icons-vue'
import { adminUserApi } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const resetPwdLoading = ref(false)
const userList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const resetPwdVisible = ref(false)
const formRef = ref(null)
const resetPwdRef = ref(null)
const currentUserId = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  role: '',
  status: ''
})

const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'user',
  status: 1
})

const resetPwdForm = reactive({
  password: '',
  confirmPassword: ''
})

const isEdit = computed(() => !!form.id)
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const resetPwdRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPwdForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await adminUserApi.getList(queryParams)
    if (res.code === 200) {
      userList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.role = ''
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
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await adminUserApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getList()
    }
  } catch (error) {
  }
}

const handleStatusChange = async (row) => {
  try {
    const res = await adminUserApi.updateStatus({
      id: row.id,
      status: row.status
    })
    if (res.code === 200) {
      ElMessage.success('状态修改成功')
    } else {
      row.status = row.status === 1 ? 0 : 1
    }
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleResetPwd = (row) => {
  currentUserId.value = row.id
  resetPwdForm.password = ''
  resetPwdForm.confirmPassword = ''
  resetPwdVisible.value = true
}

const submitResetPwd = async () => {
  const valid = await resetPwdRef.value.validate().catch(() => false)
  if (!valid) return
  
  resetPwdLoading.value = true
  try {
    const res = await adminUserApi.resetPassword({
      id: currentUserId.value,
      password: resetPwdForm.password
    })
    if (res.code === 200) {
      ElMessage.success('密码重置成功')
      resetPwdVisible.value = false
    }
  } catch (error) {
    console.error('密码重置失败:', error)
  } finally {
    resetPwdLoading.value = false
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    const api = isEdit.value ? adminUserApi.update : adminUserApi.add
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
  form.username = ''
  form.password = ''
  form.realName = ''
  form.phone = ''
  form.email = ''
  form.role = 'user'
  form.status = 1
  formRef.value?.resetFields()
}

getList()
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.user-manage {
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
      
      .header-actions {
        .add-btn {
          padding: 12px 24px;
          border-radius: 10px;
          background: $gradient-primary;
          border: none;
          font-weight: 600;
          
          &:hover {
            box-shadow: 0 4px 12px rgba($primary-color, 0.4);
            transform: translateY(-1px);
          }
        }
      }
    }
  }
  
  .search-section {
    margin-bottom: 20px;
    
    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      padding: 20px;
      background: $white;
      border-radius: 12px;
      border: 1px solid $border-light;
      
      .search-input {
        width: 220px;
        
        :deep(.el-input__wrapper) {
          border-radius: 8px;
        }
      }
      
      .filter-select {
        width: 140px;
        
        :deep(.el-input__wrapper) {
          border-radius: 8px;
        }
      }
      
      .search-btn, .reset-btn {
        border-radius: 8px;
        padding: 0 20px;
      }
      
      .search-btn {
        background: $gradient-primary;
        border: none;
      }
    }
  }
  
  .table-container {
    background: $white;
    border-radius: 16px;
    padding: 24px;
    border: 1px solid $border-light;
    
    .data-table {
      :deep(.el-table__header) {
        th {
          background: #F8FAFC;
          font-weight: 600;
          color: $text-primary;
        }
      }
      
      .user-cell {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .user-avatar {
          background: $gradient-primary;
          color: $white;
          font-weight: 600;
        }
        
        .username {
          font-weight: 500;
          color: $text-primary;
        }
      }
      
      .role-tag {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
        
        &.admin {
          background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%);
          color: $danger-dark;
        }
        
        &.user {
          background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($primary-light, 0.1) 100%);
          color: $primary-color;
        }
      }
      
      .status-switch {
        :deep(.el-switch__core) {
          border-radius: 12px;
        }
      }
      
      .action-buttons {
        display: flex;
        gap: 4px;
        
        .action-btn {
          padding: 6px 10px;
          border-radius: 6px;
          font-weight: 500;
          
          &.edit:hover { background: rgba($primary-color, 0.1); }
          &.reset:hover { background: rgba($warning-color, 0.1); }
          &.delete:hover { background: rgba($danger-color, 0.1); }
        }
      }
    }
    
    .pagination-container {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid $border-light;
    }
  }
}

.form-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    
    .el-dialog__header {
      padding: 20px 24px;
      background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
      border-bottom: 1px solid $border-light;
      margin: 0;
    }
    
    .el-dialog__body {
      padding: 24px;
    }
    
    .el-dialog__footer {
      padding: 16px 24px;
      border-top: 1px solid $border-light;
    }
  }
  
  .dialog-form {
    :deep(.el-form-item) {
      margin-bottom: 20px;
      
      .el-form-item__label {
        font-weight: 500;
        color: $text-primary;
        padding-bottom: 8px;
      }
      
      .el-input__wrapper, .el-select__wrapper {
        border-radius: 10px;
      }
      
      .full-width {
        width: 100%;
      }
      
      .status-radio {
        display: flex;
        gap: 20px;
      }
    }
    
    .select-option {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    
    .cancel-btn {
      padding: 10px 24px;
      border-radius: 10px;
    }
    
    .submit-btn {
      padding: 10px 32px;
      border-radius: 10px;
      background: $gradient-primary;
      border: none;
      
      &:hover {
        box-shadow: 0 4px 12px rgba($primary-color, 0.4);
      }
    }
  }
}

@media (max-width: 768px) {
  .user-manage {
    padding: 16px;
    
    .page-header .header-content {
      flex-direction: column;
      gap: 16px;
      
      .header-actions {
        width: 100%;
        
        .add-btn {
          width: 100%;
        }
      }
    }
    
    .search-section .search-form {
      .search-input, .filter-select {
        width: 100%;
      }
    }
    
    .table-container {
      padding: 16px;
    }
  }
}
</style>
