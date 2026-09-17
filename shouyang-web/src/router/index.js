import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

// 路由配置
const routes = [
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home/index.vue'),
        meta: { title: '首页' }
      },
      // 文旅动态
      {
        path: 'news',
        name: 'NewsList',
        component: () => import('@/views/News/List.vue'),
        meta: { title: '文旅动态' }
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('@/views/News/Detail.vue'),
        meta: { title: '资讯详情' }
      },
      // 文旅服务
      {
        path: 'service/ticket',
        name: 'TicketList',
        component: () => import('@/views/Service/TicketList.vue'),
        meta: { title: '票务预订' }
      },
      {
        path: 'service/venue',
        name: 'VenueList',
        component: () => import('@/views/Service/VenueList.vue'),
        meta: { title: '场馆预订' }
      },
      {
        path: 'service/venue/:id',
        name: 'VenueDetail',
        component: () => import('@/views/Service/VenueDetail.vue'),
        meta: { title: '场馆详情' }
      },
      {
        path: 'service/activity',
        name: 'ActivityList',
        component: () => import('@/views/Service/ActivityList.vue'),
        meta: { title: '活动报名' }
      },
      {
        path: 'service/activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/Service/ActivityDetail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'service/calendar',
        name: 'ServiceCalendar',
        component: () => import('@/views/Service/Calendar.vue'),
        meta: { title: '文旅日历' }
      },
      // 文旅时空
      {
        path: 'culture/pavilion',
        name: 'PavilionList',
        component: () => import('@/views/Culture/PavilionList.vue'),
        meta: { title: '数字展馆' }
      },
      {
        path: 'culture/heritage',
        name: 'HeritageList',
        component: () => import('@/views/Culture/HeritageList.vue'),
        meta: { title: '非遗文化' }
      },
      {
        path: 'culture/product',
        name: 'ProductList',
        component: () => import('@/views/Culture/ProductList.vue'),
        meta: { title: '文创商城' }
      },
      // 文旅时空详情页
      {
        path: 'culture/pavilion/:id',
        name: 'PavilionDetail',
        component: () => import('@/views/Culture/PavilionDetail.vue'),
        meta: { title: '展馆详情' }
      },
      {
        path: 'culture/heritage/:id',
        name: 'HeritageDetail',
        component: () => import('@/views/Culture/HeritageDetail.vue'),
        meta: { title: '非遗详情' }
      },
      {
        path: 'culture/product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/Culture/ProductDetail.vue'),
        meta: { title: '商品详情' }
      },
      // 畅游寿阳（通用列表页，通过 props 传递 type）
      {
        path: 'travel/scenic',
        name: 'ScenicList',
        component: () => import('@/views/Travel/TravelList.vue'),
        props: { type: 'scenic' },
        meta: { title: '景点推荐' }
      },
      {
        path: 'travel/food',
        name: 'FoodList',
        component: () => import('@/views/Travel/TravelList.vue'),
        props: { type: 'food' },
        meta: { title: '特色美食' }
      },
      {
        path: 'travel/hotel',
        name: 'HotelList',
        component: () => import('@/views/Travel/TravelList.vue'),
        props: { type: 'hotel' },
        meta: { title: '民宿酒店' }
      },
      {
        path: 'travel/guide',
        name: 'GuideList',
        component: () => import('@/views/Travel/TravelList.vue'),
        props: { type: 'guide' },
        meta: { title: '旅游攻略' }
      },
      // 畅游寿阳详情页（通用详情页）
      {
        path: 'travel/scenic/:id',
        name: 'ScenicDetail',
        component: () => import('@/views/Travel/TravelDetail.vue'),
        props: { type: 'scenic' },
        meta: { title: '景点详情' }
      },
      {
        path: 'travel/food/:id',
        name: 'FoodDetail',
        component: () => import('@/views/Travel/TravelDetail.vue'),
        props: { type: 'food' },
        meta: { title: '美食详情' }
      },
      {
        path: 'travel/hotel/:id',
        name: 'HotelDetail',
        component: () => import('@/views/Travel/TravelDetail.vue'),
        props: { type: 'hotel' },
        meta: { title: '酒店详情' }
      },
      {
        path: 'travel/guide/:id',
        name: 'GuideDetail',
        component: () => import('@/views/Travel/TravelDetail.vue'),
        props: { type: 'guide' },
        meta: { title: '攻略详情' }
      },
      // 文旅单位
      {
        path: 'org',
        name: 'OrgList',
        component: () => import('@/views/Org/List.vue'),
        meta: { title: '文旅单位' }
      },
      // 搜索
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/Search/index.vue'),
        meta: { title: '搜索' }
      },
      // 用户中心
      {
        path: 'user',
        name: 'UserCenter',
        component: () => import('@/views/User/Profile.vue'),
        meta: { title: '个人中心', requireAuth: true }
      },
      {
        path: 'user/favorite',
        name: 'UserFavorite',
        component: () => import('@/views/User/Favorite.vue'),
        meta: { title: '我的收藏', requireAuth: true }
      },
      {
        path: 'user/activity',
        name: 'UserActivity',
        component: () => import('@/views/User/Activity.vue'),
        meta: { title: '我的报名', requireAuth: true }
      }
    ]
  },
  // 登录注册（独立布局，无导航栏）
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 滚动行为：切换路由时滚动到顶部
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 寿阳文旅云` : '寿阳文旅云'

  // 检查是否需要登录
  const token = getToken()
  if (to.meta.requireAuth && !token) {
    // 未登录，跳转到登录页，携带回跳路径
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

export default router
