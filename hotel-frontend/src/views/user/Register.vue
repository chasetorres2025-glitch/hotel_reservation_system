<template>
  <div class="register-page">
    <div class="register-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
    </div>
    
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <div class="logo-icon">
            <el-icon :size="32"><House /></el-icon>
          </div>
          <h2 class="title">创建账号</h2>
          <p class="subtitle">加入我们，开启您的旅程</p>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item prop="password">
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入密码"
                  :prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="确认密码"
                  :prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item prop="realName">
            <el-input
              v-model="form.realName"
              placeholder="请输入真实姓名"
              :prefix-icon="UserFilled"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              :prefix-icon="Phone"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              placeholder="请输入邮箱（选填）"
              :prefix-icon="Message"
              size="large"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="register-btn"
              @click="handleRegister"
            >
              <span v-if="!loading">立即注册</span>
              <span v-else>注册中...</span>
            </el-button>
          </el-form-item>
          
          <div class="register-footer">
            <span>已有账号？</span>
            <el-button type="primary" link @click="router.push('/user/login')">
              立即登录
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, User, Lock, UserFilled, Phone, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
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

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const result = await userStore.register({
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone,
      email: form.email
    })
    if (result.success) {
      ElMessage.success('注册成功，请登录')
      router.push('/user/login')
    } else {
      ElMessage.error(result.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $gradient-primary;
  position: relative;
  overflow: hidden;
  padding: $spacing-2xl 0;
  
  .register-decoration {
    position: absolute;
    inset: 0;
    pointer-events: none;
    
    .decoration-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      
      &.circle-1 {
        width: 500px;
        height: 500px;
        top: -200px;
        right: -150px;
        animation: float 10s ease-in-out infinite;
      }
      
      &.circle-2 {
        width: 400px;
        height: 400px;
        bottom: -150px;
        left: -100px;
        animation: float 8s ease-in-out infinite reverse;
      }
    }
  }
  
  .register-container {
    position: relative;
    z-index: 1;
  }
  
  .register-card {
    width: 460px;
    background: $white;
    border-radius: $border-radius-2xl;
    padding: $spacing-2xl $spacing-3xl;
    box-shadow: $shadow-xl;
    animation: scaleIn 0.5s ease forwards;
    
    .register-header {
      text-align: center;
      margin-bottom: $spacing-xl;
      
      .logo-icon {
        width: 64px;
        height: 64px;
        background: $gradient-primary;
        border-radius: $border-radius-xl;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $white;
        margin: 0 auto $spacing-lg;
        box-shadow: $shadow-primary;
      }
      
      .title {
        font-size: $font-size-2xl;
        font-weight: $font-weight-bold;
        color: $text-primary;
        margin: 0 0 $spacing-sm;
      }
      
      .subtitle {
        font-size: $font-size-base;
        color: $text-secondary;
        margin: 0;
      }
    }
    
    .register-form {
      .el-form-item {
        margin-bottom: $spacing-md;
      }
      
      .register-btn {
        width: 100%;
        height: 48px;
        font-size: $font-size-md;
        font-weight: $font-weight-semibold;
        border-radius: $border-radius-lg;
        margin-top: $spacing-sm;
        transition: all $transition-base;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: $shadow-primary;
        }
      }
    }
    
    .register-footer {
      text-align: center;
      font-size: $font-size-base;
      color: $text-secondary;
      margin-top: $spacing-lg;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(3deg);
  }
}
</style>
