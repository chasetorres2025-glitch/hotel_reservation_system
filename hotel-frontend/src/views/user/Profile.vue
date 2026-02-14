<template>
  <div class="profile-page">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2 class="page-title">个人中心</h2>
          <p class="page-subtitle">管理您的个人信息和账户设置</p>
        </div>
      </div>
    </div>
    
    <div class="profile-content">
      <div class="profile-sidebar">
        <div class="profile-card">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :icon="UserFilled" class="user-avatar" />
              <div class="avatar-badge">
                <el-icon><Edit /></el-icon>
              </div>
            </div>
            <h3 class="username">{{ userInfo.username }}</h3>
            <p class="real-name">{{ userInfo.realName || '未设置姓名' }}</p>
            <div class="role-tag" :class="userInfo.role">
              <el-icon v-if="userInfo.role === 'admin'"><Star /></el-icon>
              <el-icon v-else><User /></el-icon>
              <span>{{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}</span>
            </div>
          </div>
          
          <div class="info-list">
            <div class="info-item">
              <div class="info-icon">
                <el-icon><Phone /></el-icon>
              </div>
              <div class="info-content">
                <span class="info-label">手机号码</span>
                <span class="info-value">{{ userInfo.phone || '未设置' }}</span>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">
                <el-icon><Message /></el-icon>
              </div>
              <div class="info-content">
                <span class="info-label">电子邮箱</span>
                <span class="info-value">{{ userInfo.email || '未设置' }}</span>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="info-content">
                <span class="info-label">注册时间</span>
                <span class="info-value">{{ userInfo.createTime }}</span>
              </div>
            </div>
          </div>
          
          <div class="quick-stats">
            <div class="stat-item">
              <span class="stat-value">{{ orderStats.total }}</span>
              <span class="stat-label">总订单</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ orderStats.completed }}</span>
              <span class="stat-label">已完成</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ orderStats.pending }}</span>
              <span class="stat-label">待处理</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="profile-main">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane name="info">
            <template #label>
              <div class="tab-label">
                <el-icon><User /></el-icon>
                <span>基本信息</span>
              </div>
            </template>
            
            <div class="tab-content">
              <div class="section-header">
                <h4>基本信息</h4>
                <p>更新您的个人资料信息</p>
              </div>
              
              <el-form 
                ref="infoFormRef" 
                :model="infoForm" 
                :rules="infoRules" 
                label-position="top"
                class="info-form"
              >
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="用户名">
                      <el-input v-model="userInfo.username" disabled class="disabled-input">
                        <template #prefix>
                          <el-icon><User /></el-icon>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="真实姓名" prop="realName">
                      <el-input v-model="infoForm.realName" placeholder="请输入真实姓名">
                        <template #prefix>
                          <el-icon><Avatar /></el-icon>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="手机号码" prop="phone">
                      <el-input v-model="infoForm.phone" placeholder="请输入手机号码">
                        <template #prefix>
                          <el-icon><Phone /></el-icon>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="电子邮箱" prop="email">
                      <el-input v-model="infoForm.email" placeholder="请输入电子邮箱">
                        <template #prefix>
                          <el-icon><Message /></el-icon>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item class="form-actions">
                  <el-button type="primary" :loading="infoLoading" @click="updateInfo" class="submit-btn">
                    <el-icon v-if="!infoLoading"><Check /></el-icon>
                    <span>保存修改</span>
                  </el-button>
                  <el-button @click="resetInfoForm" class="reset-btn">
                    <el-icon><RefreshRight /></el-icon>
                    <span>重置</span>
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          
          <el-tab-pane name="password">
            <template #label>
              <div class="tab-label">
                <el-icon><Lock /></el-icon>
                <span>修改密码</span>
              </div>
            </template>
            
            <div class="tab-content">
              <div class="section-header">
                <h4>修改密码</h4>
                <p>定期修改密码可以提高账户安全性</p>
              </div>
              
              <el-form 
                ref="pwdFormRef" 
                :model="pwdForm" 
                :rules="pwdRules" 
                label-position="top"
                class="pwd-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input 
                    v-model="pwdForm.oldPassword" 
                    type="password" 
                    placeholder="请输入原密码" 
                    show-password
                  >
                    <template #prefix>
                      <el-icon><Key /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
                
                <el-form-item label="新密码" prop="newPassword">
                  <el-input 
                    v-model="pwdForm.newPassword" 
                    type="password" 
                    placeholder="请输入新密码（6-20个字符）" 
                    show-password
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
                
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input 
                    v-model="pwdForm.confirmPassword" 
                    type="password" 
                    placeholder="请再次输入新密码" 
                    show-password
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
                
                <el-form-item class="form-actions">
                  <el-button type="primary" :loading="pwdLoading" @click="updatePassword" class="submit-btn">
                    <el-icon v-if="!pwdLoading"><Check /></el-icon>
                    <span>修改密码</span>
                  </el-button>
                  <el-button @click="resetPwdForm" class="reset-btn">
                    <el-icon><RefreshRight /></el-icon>
                    <span>重置</span>
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  UserFilled, Phone, Message, Calendar, User, Edit, 
  Star, Lock, Check, RefreshRight, Key, Avatar
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { orderApi } from '@/api/room'

const userStore = useUserStore()

const activeTab = ref('info')
const infoLoading = ref(false)
const pwdLoading = ref(false)
const infoFormRef = ref(null)
const pwdFormRef = ref(null)

const userInfo = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: '',
  createTime: ''
})

const orderStats = ref({
  total: 0,
  completed: 0,
  pending: 0
})

const infoForm = reactive({
  realName: '',
  phone: '',
  email: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const infoRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const pwdRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const getUserInfo = async () => {
  try {
    const res = await userApi.getProfile()
    if (res.code === 200) {
      userInfo.value = res.data
      infoForm.realName = res.data.realName
      infoForm.phone = res.data.phone
      infoForm.email = res.data.email
      userStore.setUserInfo(res.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const getOrderStats = async () => {
  try {
    const res = await orderApi.getList({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
      const orders = res.data.list || []
      orderStats.value = {
        total: orders.length,
        completed: orders.filter(o => o.status === 4).length,
        pending: orders.filter(o => [0, 1, 2].includes(o.status)).length
      }
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

const updateInfo = async () => {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  infoLoading.value = true
  try {
    const res = await userApi.updateProfile(infoForm)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      getUserInfo()
    }
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    infoLoading.value = false
  }
}

const updatePassword = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  pwdLoading.value = true
  try {
    const res = await userApi.updatePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      resetPwdForm()
    }
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    pwdLoading.value = false
  }
}

const resetInfoForm = () => {
  infoForm.realName = userInfo.value.realName
  infoForm.phone = userInfo.value.phone
  infoForm.email = userInfo.value.email
  infoFormRef.value?.clearValidate()
}

const resetPwdForm = () => {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdFormRef.value?.clearValidate()
}

onMounted(() => {
  getUserInfo()
  getOrderStats()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.profile-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  
  .page-header {
    margin-bottom: 24px;
    
    .header-content {
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
    }
  }
  
  .profile-content {
    display: flex;
    gap: 24px;
    
    .profile-sidebar {
      width: 320px;
      flex-shrink: 0;
      
      .profile-card {
        background: $white;
        border-radius: 16px;
        padding: 32px 24px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        border: 1px solid $border-light;
        
        .avatar-section {
          text-align: center;
          padding-bottom: 24px;
          border-bottom: 1px solid $border-light;
          
          .avatar-wrapper {
            position: relative;
            display: inline-block;
            margin-bottom: 16px;
            
            .user-avatar {
              background: $gradient-primary;
              border: 4px solid $white;
              box-shadow: 0 4px 12px rgba($primary-color, 0.3);
            }
            
            .avatar-badge {
              position: absolute;
              bottom: 0;
              right: 0;
              width: 32px;
              height: 32px;
              background: $white;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
              cursor: pointer;
              transition: all 0.3s ease;
              
              .el-icon {
                font-size: 16px;
                color: $primary-light;
              }
              
              &:hover {
                transform: scale(1.1);
                background: $primary-light;
                
                .el-icon {
                  color: $white;
                }
              }
            }
          }
          
          .username {
            font-size: 22px;
            font-weight: 700;
            color: $text-primary;
            margin: 0 0 4px;
          }
          
          .real-name {
            font-size: 14px;
            color: $text-secondary;
            margin: 0 0 12px;
          }
          
          .role-tag {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 13px;
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
        }
        
        .info-list {
          padding: 24px 0;
          
          .info-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 0;
            
            &:not(:last-child) {
              border-bottom: 1px dashed $border-light;
            }
            
            .info-icon {
              width: 40px;
              height: 40px;
              background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
              border-radius: 10px;
              display: flex;
              align-items: center;
              justify-content: center;
              
              .el-icon {
                font-size: 18px;
                color: $primary-light;
              }
            }
            
            .info-content {
              flex: 1;
              
              .info-label {
                display: block;
                font-size: 12px;
                color: $text-secondary;
                margin-bottom: 2px;
              }
              
              .info-value {
                font-size: 14px;
                color: $text-primary;
                font-weight: 500;
              }
            }
          }
        }
        
        .quick-stats {
          display: flex;
          gap: 12px;
          padding-top: 20px;
          border-top: 1px solid $border-light;
          
          .stat-item {
            flex: 1;
            text-align: center;
            padding: 16px 8px;
            background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
            border-radius: 12px;
            
            .stat-value {
              display: block;
              font-size: 24px;
              font-weight: 700;
              color: $primary-color;
              line-height: 1;
              margin-bottom: 4px;
            }
            
            .stat-label {
              font-size: 12px;
              color: $text-secondary;
            }
          }
        }
      }
    }
    
    .profile-main {
      flex: 1;
      min-width: 0;
      
      .profile-tabs {
        background: $white;
        border-radius: 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        border: 1px solid $border-light;
        overflow: hidden;
        
        :deep(.el-tabs__header) {
          margin: 0;
          padding: 0 24px;
          background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
          border-bottom: 1px solid $border-light;
        }
        
        :deep(.el-tabs__nav-wrap)::after {
          display: none;
        }
        
        :deep(.el-tabs__item) {
          height: 56px;
          line-height: 56px;
          padding: 0 24px;
          
          &.is-active {
            color: $primary-color;
            font-weight: 600;
          }
        }
        
        :deep(.el-tabs__active-bar) {
          height: 3px;
          border-radius: 2px;
          background: $gradient-primary;
        }
        
        :deep(.el-tabs__content) {
          padding: 0;
        }
        
        .tab-label {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .el-icon {
            font-size: 16px;
          }
        }
        
        .tab-content {
          padding: 32px;
          
          .section-header {
            margin-bottom: 32px;
            
            h4 {
              font-size: 18px;
              font-weight: 600;
              color: $text-primary;
              margin: 0 0 8px;
            }
            
            p {
              font-size: 14px;
              color: $text-secondary;
              margin: 0;
            }
          }
          
          .info-form, .pwd-form {
            max-width: 600px;
            
            :deep(.el-form-item) {
              margin-bottom: 24px;
              
              .el-form-item__label {
                font-weight: 500;
                color: $text-primary;
                padding-bottom: 8px;
              }
              
              .el-input {
                .el-input__wrapper {
                  border-radius: 10px;
                  padding: 4px 12px;
                  box-shadow: none;
                  border: 1px solid $border-color;
                  transition: all 0.3s ease;
                  
                  &:hover {
                    border-color: $primary-light;
                  }
                  
                  &.is-focus {
                    border-color: $primary-light;
                    box-shadow: 0 0 0 3px rgba($primary-color, 0.1);
                  }
                }
                
                .el-input__prefix {
                  color: $text-secondary;
                }
              }
              
              .disabled-input {
                .el-input__wrapper {
                  background: #F8FAFC;
                  cursor: not-allowed;
                  
                  &:hover {
                    border-color: $border-color;
                  }
                }
              }
            }
            
            .form-actions {
              margin-top: 32px;
              padding-top: 24px;
              border-top: 1px solid $border-light;
              
              .submit-btn {
                padding: 12px 32px;
                border-radius: 10px;
                background: $gradient-primary;
                border: none;
                font-weight: 600;
                
                &:hover {
                  box-shadow: 0 4px 12px rgba($primary-color, 0.4);
                  transform: translateY(-1px);
                }
              }
              
              .reset-btn {
                padding: 12px 24px;
                border-radius: 10px;
                border: 1px solid $border-color;
                background: $white;
                color: $text-regular;
                font-weight: 500;
                
                &:hover {
                  border-color: $primary-light;
                  color: $primary-light;
                }
              }
            }
          }
        }
      }
    }
  }
}

@media (max-width: 992px) {
  .profile-page {
    .profile-content {
      flex-direction: column;
      
      .profile-sidebar {
        width: 100%;
      }
    }
  }
}

@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
    
    .profile-content {
      .profile-sidebar .profile-card {
        padding: 24px 16px;
        
        .quick-stats {
          flex-wrap: wrap;
          
          .stat-item {
            min-width: calc(50% - 6px);
          }
        }
      }
      
      .profile-main .profile-tabs .tab-content {
        padding: 24px 16px;
        
        .info-form, .pwd-form {
          :deep(.el-form-item) {
            .el-col {
              width: 100%;
            }
          }
        }
      }
    }
  }
}
</style>
