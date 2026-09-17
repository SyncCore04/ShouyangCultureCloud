import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/request'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'banner',
        name: 'Banner',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/News/index.vue'),
        meta: { title: '资讯管理', icon: 'Document' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      },
      {
        path: 'ticket',
        name: 'Ticket',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '票务管理', icon: 'Ticket' }
      },
      {
        path: 'venue',
        name: 'Venue',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '场馆管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '活动管理', icon: 'Calendar' }
      },
      {
        path: 'pavilion',
        name: 'Pavilion',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '展馆管理', icon: 'PictureFilled' }
      },
      {
        path: 'heritage',
        name: 'Heritage',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '非遗管理', icon: 'Star' }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '文创管理', icon: 'Goods' }
      },
      {
        path: 'scenic',
        name: 'Scenic',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '景点管理', icon: 'Location' }
      },
      {
        path: 'food',
        name: 'Food',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '美食管理', icon: 'Food' }
      },
      {
        path: 'hotel',
        name: 'Hotel',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '酒店管理', icon: 'HomeFilled' }
      },
      {
        path: 'guide',
        name: 'Guide',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '攻略管理', icon: 'Guide' }
      },
      {
        path: 'org',
        name: 'Org',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '单位管理', icon: 'Building' }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '个人设置', icon: 'Setting' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 寿阳文旅云后台` : '寿阳文旅云后台'

  const token = getToken()
  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    if (!token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
    } else {
      next()
    }
  }
})

export default router
