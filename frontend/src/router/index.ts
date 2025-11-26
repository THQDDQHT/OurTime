import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/DesktopHome.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/home-old',
    name: 'HomeOld',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/albums',
    redirect: '/'
  },
  {
    path: '/albums/:id',
    name: 'AlbumDetail',
    component: () => import('@/views/AlbumDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/timeline',
    name: 'Timeline',
    component: () => import('@/views/Timeline.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated()) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated()) {
    next('/')
  } else {
    next()
  }
})

export default router

