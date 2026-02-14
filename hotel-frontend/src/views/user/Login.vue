<template>
  <div class="login-page">
    <div class="login-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="logo-icon">
            <el-icon :size="32"><House /></el-icon>
          </div>
          <h2 class="title">酒店预订系统</h2>
          <p class="subtitle">欢迎回来，请登录您的账号</p>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
          
          <div class="login-footer">
            <span>还没有账号？</span>
            <el-button type="primary" link @click="router.push('/user/register')">
              立即注册
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const result = await userStore.login(form)
    if (result.success) {
      ElMessage.success('登录成功')
      setTimeout(() => {
        if (userStore.isAdmin) {
          window.location.href = '/admin/user'
        } else {
          window.location.href = '/user/rooms'
        }
      }, 100)
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $gradient-primary;
  position: relative;
  overflow: hidden;
  
  .login-decoration {
    position: absolute;
    inset: 0;
    pointer-events: none;
    
    .decoration-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      
      &.circle-1 {
        width: 400px;
        height: 400px;
        top: -100px;
        right: -100px;
        animation: float 8s ease-in-out infinite;
      }
      
      &.circle-2 {
        width: 300px;
        height: 300px;
        bottom: -50px;
        left: -50px;
        animation: float 6s ease-in-out infinite reverse;
      }
      
      &.circle-3 {
        width: 200px;
        height: 200px;
        top: 50%;
        left: 10%;
        animation: float 7s ease-in-out infinite;
      }
    }
  }
  
  .login-container {
    position: relative;
    z-index: 1;
  }
  
  .login-card {
    width: 420px;
    background: $white;
    border-radius: $border-radius-2xl;
    padding: $spacing-3xl;
    box-shadow: $shadow-xl;
    animation: scaleIn 0.5s ease forwards;
    
    .login-header {
      text-align: center;
      margin-bottom: $spacing-2xl;
      
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
    
    .login-form {
      .el-form-item {
        margin-bottom: $spacing-lg;
      }
      
      .login-btn {
        width: 100%;
        height: 48px;
        font-size: $font-size-md;
        font-weight: $font-weight-semibold;
        border-radius: $border-radius-lg;
        letter-spacing: 4px;
        transition: all $transition-base;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: $shadow-primary;
        }
      }
    }
    
    .login-footer {
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
    transform: translateY(-20px) rotate(5deg);
  }
}
</style>
