import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/user/login'
  },
  {
    path: '/user',
    component: () => import('@/layouts/UserLayout.vue'),
    children: [
      {
        path: 'login',
        name: 'UserLogin',
        component: () => import('@/views/user/Login.vue'),
        meta: { title: '登录' }
      },
      {
        path: 'register',
        name: 'UserRegister',
        component: () => import('@/views/user/Register.vue'),
        meta: { title: '注册' }
      },
      {
        path: 'rooms',
        name: 'RoomList',
        component: () => import('@/views/user/RoomList.vue'),
        meta: { title: '客房列表', requiresAuth: true }
      },
      {
        path: 'room/:id',
        name: 'RoomDetail',
        component: () => import('@/views/user/RoomDetail.vue'),
        meta: { title: '客房详情', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'MyOrders',
        component: () => import('@/views/user/MyOrders.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/user'
      },
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'room',
        name: 'RoomManage',
        component: () => import('@/views/admin/RoomManage.vue'),
        meta: { title: '客房管理' }
      },
      {
        path: 'order',
        name: 'OrderManage',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

let userStoreInstance = null

const getUserStore = async () => {
  if (!userStoreInstance) {
    const { useUserStore } = await import('@/stores/user')
    userStoreInstance = useUserStore()
  }
  return userStoreInstance
}

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 酒店管理系统` : '酒店管理系统'
  
  const store = await getUserStore()
  const isLoggedIn = store.isLoggedIn
  const isAdmin = store.isAdmin
  
  if (isLoggedIn && (to.path === '/user/login' || to.path === '/user/register')) {
    if (isAdmin) {
      next('/admin/user')
    } else {
      next('/user/rooms')
    }
    return
  }
  
  if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      next({
        path: '/user/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    if (to.meta.requiresAdmin && !isAdmin) {
      next('/user/rooms')
      return
    }
  }
  
  next()
})

export default router
