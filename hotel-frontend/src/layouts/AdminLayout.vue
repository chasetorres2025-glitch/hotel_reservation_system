<template>
  <div class="admin-layout">
    <el-header class="admin-header">
      <div class="header-left">
        <div class="logo">
          <div class="logo-icon">
            <el-icon :size="22"><House /></el-icon>
          </div>
          <span class="logo-text">酒店管理系统</span>
        </div>
      </div>
      <div class="header-right">
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
      </div>
    </el-header>
    
    <el-container class="admin-container">
      <el-aside :width="isCollapse ? '72px' : '220px'" class="admin-aside">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="admin-menu"
        >
          <el-menu-item index="/admin/user">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/room">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>客房管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/order">
            <el-icon><Document /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/statistics">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>数据统计</template>
          </el-menu-item>
        </el-menu>
        
        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon :size="18">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span v-if="!isCollapse" class="collapse-text">收起菜单</span>
        </div>
      </el-aside>
      
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import {
  House,
  User,
  ArrowDown,
  SwitchButton,
  OfficeBuilding,
  Document,
  DataAnalysis,
  Fold,
  Expand
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

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

.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: $bg-color;
}

.admin-header {
  height: 64px;
  background: $white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 $spacing-xl;
  box-shadow: $shadow-sm;
  z-index: $z-sticky;
  border-bottom: 1px solid $border-light;
  
  .header-left {
    .logo {
      display: flex;
      align-items: center;
      
      .logo-icon {
        width: 36px;
        height: 36px;
        background: $gradient-primary;
        border-radius: $border-radius-md;
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
  
  .header-right {
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
      }
    }
  }
}

.admin-container {
  flex: 1;
  overflow: hidden;
}

.admin-aside {
  background: $white;
  box-shadow: $shadow-sm;
  display: flex;
  flex-direction: column;
  transition: width $transition-base;
  border-right: 1px solid $border-light;
  
  .admin-menu {
    flex: 1;
    border-right: none;
    padding: $spacing-sm 0;
    
    .el-menu-item {
      height: 52px;
      line-height: 52px;
      margin: $spacing-xs $spacing-sm;
      border-radius: $border-radius-md;
      transition: all $transition-base;
      
      &.is-active {
        background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($primary-lighter, 0.05) 100%);
        color: $primary-color;
        font-weight: $font-weight-semibold;
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 24px;
          background: $gradient-primary;
          border-radius: 0 $border-radius-full $border-radius-full 0;
        }
      }
      
      &:hover {
        background-color: rgba($primary-color, 0.04);
        color: $primary-color;
      }
    }
  }
  
  .collapse-btn {
    height: 52px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-sm;
    cursor: pointer;
    border-top: 1px solid $border-light;
    color: $text-secondary;
    transition: all $transition-base;
    
    &:hover {
      color: $primary-color;
      background-color: $bg-dark;
    }
    
    .collapse-text {
      font-size: $font-size-sm;
    }
  }
}

.admin-main {
  background: $bg-color;
  padding: $spacing-xl;
  overflow-y: auto;
}
</style>
