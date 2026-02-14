<template>
  <div class="user-layout">
    <el-header class="user-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo" @click="router.push('/user/rooms')">
            <div class="logo-icon">
              <el-icon :size="24"><House /></el-icon>
            </div>
            <span class="logo-text">酒店预订系统</span>
          </div>
        </div>
        
        <div class="header-center">
          <el-menu
            v-if="userStore.isLoggedIn"
            :default-active="activeMenu"
            mode="horizontal"
            router
            class="nav-menu"
          >
            <el-menu-item index="/user/rooms">
              <el-icon><OfficeBuilding /></el-icon>
              <span>客房预订</span>
            </el-menu-item>
            <el-menu-item index="/user/orders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="/user/profile">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
          </el-menu>
        </div>
        
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-info">
                <el-avatar :size="36" class="user-avatar">
                  {{ (userStore.realName || userStore.username)?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="username">{{ userStore.realName || userStore.username }}</span>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" class="login-btn" @click="router.push('/user/login')">
              登录
            </el-button>
            <el-button class="register-btn" @click="router.push('/user/register')">
              注册
            </el-button>
          </template>
        </div>
      </div>
    </el-header>
    
    <el-main class="user-main">
      <router-view />
    </el-main>
    
    <el-footer class="user-footer">
      <div class="footer-content">
        <div class="footer-info">
          <div class="footer-logo">
            <el-icon :size="20"><House /></el-icon>
            <span>酒店管理系统</span>
          </div>
          <p class="copyright">Copyright 2026 酒店管理系统 All Rights Reserved</p>
        </div>
        <div class="footer-links">
          <a href="#">关于我们</a>
          <a href="#">联系方式</a>
          <a href="#">帮助中心</a>
          <a href="#">隐私政策</a>
        </div>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import {
  House,
  User,
  ArrowDown,
  SwitchButton,
  OfficeBuilding,
  Document
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/user/room')) {
    return '/user/rooms'
  }
  return path
})

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logout()
      router.push('/user/login')
    } catch (error) {
      // 用户取消
    }
  } else if (command === 'profile') {
    router.push('/user/profile')
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/main.scss';

.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: $bg-color;
}

.user-header {
  height: 72px;
  background: $white;
  box-shadow: $shadow-sm;
  position: sticky;
  top: 0;
  z-index: $z-sticky;
  border-bottom: 1px solid $border-light;
  
  .header-content {
    max-width: 1280px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 $spacing-xl;
  }
  
  .header-left {
    .logo {
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: opacity $transition-base;
      
      &:hover {
        opacity: 0.85;
      }
      
      .logo-icon {
        width: 40px;
        height: 40px;
        background: $gradient-primary;
        border-radius: $border-radius-lg;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $white;
        margin-right: $spacing-sm;
      }
      
      .logo-text {
        font-size: $font-size-lg;
        font-weight: $font-weight-semibold;
        color: $text-primary;
      }
    }
  }
  
  .header-center {
    .nav-menu {
      background: transparent;
      border-bottom: none;
      
      .el-menu-item {
        height: 72px;
        line-height: 72px;
        color: $text-regular;
        border-bottom: none;
        padding: 0 $spacing-lg;
        margin: 0 $spacing-xs;
        border-radius: $border-radius-md $border-radius-md 0 0;
        transition: all $transition-base;
        
        &:hover {
          background-color: rgba($primary-color, 0.04);
          color: $primary-color;
        }
        
        &.is-active {
          background-color: rgba($primary-color, 0.06);
          color: $primary-color;
          border-bottom: 3px solid $primary-color;
        }
        
        .el-icon {
          margin-right: $spacing-xs;
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: $spacing-md;
    
    .login-btn {
      padding: $spacing-sm $spacing-lg;
    }
    
    .register-btn {
      padding: $spacing-sm $spacing-lg;
    }
    
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: $spacing-xs $spacing-md;
      border-radius: $border-radius-full;
      transition: background-color $transition-base;
      
      &:hover {
        background-color: $bg-dark;
      }
      
      .user-avatar {
        background: $gradient-primary;
        color: $white;
        font-weight: $font-weight-semibold;
      }
      
      .username {
        margin: 0 $spacing-sm;
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $text-primary;
      }
      
      .arrow-icon {
        color: $text-secondary;
        transition: transform $transition-base;
      }
    }
  }
}

.user-main {
  flex: 1;
  background: $bg-color;
  padding: $spacing-xl;
  overflow-y: auto;
}

.user-footer {
  height: auto;
  background: $gradient-dark;
  padding: $spacing-2xl $spacing-xl;
  
  .footer-content {
    max-width: 1280px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    @media (max-width: 768px) {
      flex-direction: column;
      gap: $spacing-lg;
      text-align: center;
    }
  }
  
  .footer-info {
    .footer-logo {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      color: $white;
      font-size: $font-size-md;
      font-weight: $font-weight-semibold;
      margin-bottom: $spacing-sm;
    }
    
    .copyright {
      color: rgba(255, 255, 255, 0.6);
      font-size: $font-size-xs;
      margin: 0;
    }
  }
  
  .footer-links {
    display: flex;
    gap: $spacing-xl;
    
    a {
      color: rgba(255, 255, 255, 0.7);
      text-decoration: none;
      font-size: $font-size-sm;
      transition: color $transition-base;
      
      &:hover {
        color: $white;
      }
    }
  }
}
</style>
