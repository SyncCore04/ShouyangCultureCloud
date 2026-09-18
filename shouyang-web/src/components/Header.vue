<template>
  <header class="site-header" :class="{ 'is-scrolled': isScrolled }">
    <div class="header-container">
      <!-- 左侧：Logo + 网站名称 -->
      <div class="header-left" @click="goHome">
        <div class="logo">
          <el-icon :size="28" color="#2c3e6b"><Picture /></el-icon>
        </div>
        <span class="site-name">寿阳文旅云</span>
      </div>

      <!-- 中间：主导航菜单（桌面端） -->
      <nav class="header-nav">
        <template v-for="item in navItems" :key="item.path">
          <!-- 没有子菜单的：直接用 router-link -->
          <router-link
            v-if="!item.children"
            :to="item.path"
            class="nav-item"
            :class="{ active: isActive(item) }"
          >
            <span class="nav-text">{{ item.name }}</span>
          </router-link>

          <!-- 有子菜单的：用普通 div，只 hover 显示下拉，不点击跳转 -->
          <div
            v-else
            class="nav-item has-dropdown"
            @mouseenter="activeDropdown = item.path"
            @mouseleave="activeDropdown = ''"
          >
            <span class="nav-text">{{ item.name }}</span>
            <el-icon class="nav-arrow"><ArrowDown /></el-icon>

            <!-- 下拉子菜单 -->
            <div v-if="activeDropdown === item.path" class="dropdown-menu">
              <router-link
                v-for="child in item.children"
                :key="child.path"
                :to="child.path"
                class="dropdown-item"
              >
                <el-icon class="dropdown-icon"><component :is="child.icon" /></el-icon>
                <span>{{ child.name }}</span>
              </router-link>
            </div>
          </div>
        </template>
      </nav>

      <!-- 右侧：搜索 + 用户操作 -->
      <div class="header-right">
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索景点、美食、资讯..."
            size="default"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 未登录：登录/注册按钮 -->
        <template v-if="!userStore.isLogin">
          <el-button type="primary" plain class="auth-btn" @click="$router.push('/login')">
            登录
          </el-button>
          <el-button type="primary" class="auth-btn" @click="$router.push('/register')">
            注册
          </el-button>
        </template>

        <!-- 已登录：用户头像 + 下拉菜单 -->
        <div v-else class="user-menu" @mouseenter="userDropdownVisible = true" @mouseleave="userDropdownVisible = false">
          <div class="user-info">
            <el-avatar :size="32" :src="userStore.avatar">
              {{ userStore.nickname.charAt(0) }}
            </el-avatar>
            <span class="user-nickname">{{ userStore.nickname }}</span>
            <el-icon class="user-arrow"><ArrowDown /></el-icon>
          </div>
          <div v-if="userDropdownVisible" class="user-dropdown">
            <router-link to="/user" class="user-dropdown-item">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </router-link>
            <router-link to="/user/favorite" class="user-dropdown-item">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </router-link>
            <router-link to="/user/activity" class="user-dropdown-item">
              <el-icon><Tickets /></el-icon>
              <span>我的报名</span>
            </router-link>
            <div class="dropdown-divider"></div>
            <div class="user-dropdown-item" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </div>
          </div>
        </div>

        <!-- 移动端汉堡菜单按钮 -->
        <div class="mobile-menu-btn" @click="mobileMenuVisible = !mobileMenuVisible">
          <el-icon :size="24"><Menu /></el-icon>
        </div>
      </div>
    </div>

    <!-- 移动端导航菜单 -->
    <div v-if="mobileMenuVisible" class="mobile-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="mobile-nav-item"
        @click="mobileMenuVisible = false"
      >
        {{ item.name }}
      </router-link>
      <div class="mobile-nav-sub" v-for="item in navItems.filter(i => i.children)" :key="item.path">
        <div class="mobile-nav-title">{{ item.name }}</div>
        <router-link
          v-for="child in item.children"
          :key="child.path"
          :to="child.path"
          class="mobile-nav-child"
          @click="mobileMenuVisible = false"
        >
          {{ child.name }}
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Picture, ArrowDown, Search, User, Star, SwitchButton, Menu,
  Ticket, OfficeBuilding, Calendar, Monitor, ShoppingCart,
  Place, Dish, House, Compass, Collection
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 滚动状态
const isScrolled = ref(false)
const handleScroll = () => {
  isScrolled.value = window.scrollY > 10
}
onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))

// 搜索
const searchKeyword = ref('')
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  router.push({ path: '/search', query: { keyword: searchKeyword.value.trim() } })
}

// 下拉菜单状态
const activeDropdown = ref('')
const userDropdownVisible = ref(false)
const mobileMenuVisible = ref(false)

// 导航菜单配置
const navItems = [
  { name: '首页', path: '/' },
  { name: '文旅动态', path: '/news' },
  {
    name: '文旅服务',
    path: '/service',
    children: [
      { name: '票务预订', path: '/service/ticket', icon: Ticket },
      { name: '场馆预订', path: '/service/venue', icon: OfficeBuilding },
      { name: '活动报名', path: '/service/activity', icon: Calendar },
      { name: '文旅日历', path: '/service/calendar', icon: Calendar }
    ]
  },
  {
    name: '文旅时空',
    path: '/culture',
    children: [
      { name: '数字展馆', path: '/culture/pavilion', icon: Monitor },
      { name: '非遗文化', path: '/culture/heritage', icon: Collection },
      { name: '文创商城', path: '/culture/product', icon: ShoppingCart }
    ]
  },
  {
    name: '畅游寿阳',
    path: '/travel',
    children: [
      { name: '景点推荐', path: '/travel/scenic', icon: Place },
      { name: '特色美食', path: '/travel/food', icon: Dish },
      { name: '民宿酒店', path: '/travel/hotel', icon: House },
      { name: '旅游攻略', path: '/travel/guide', icon: Compass }
    ]
  },
  { name: '文旅单位', path: '/org' }
]

// 判断导航项是否高亮（包含子路径）
const isActive = (item) => {
  if (item.path === '/') return route.path === '/'
  return route.path.startsWith(item.path)
}

// 返回首页
const goHome = () => router.push('/')

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.site-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #fff;
  transition: all 0.3s ease;

  &.is-scrolled {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

/* 左侧 Logo */
.header-left {
  display: flex;
  align-items: center;
  cursor: pointer;
  flex-shrink: 0;

  .logo {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #2c3e6b, #4a6fa5);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 10px;

    .el-icon {
      color: #fff !important;
    }
  }

  .site-name {
    font-size: 20px;
    font-weight: 700;
    color: #2c3e6b;
    letter-spacing: 1px;
  }
}

/* 中间导航 */
.header-nav {
  display: flex;
  align-items: center;
  height: 100%;
  flex: 1;
  justify-content: center;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  height: 64px;
  padding: 0 18px;
  color: #455a64;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
  cursor: pointer;

  &:hover {
    color: #2c3e6b;
  }

  &.active {
    color: #2c3e6b;
    font-weight: 600;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 30px;
      height: 3px;
      background: #2c3e6b;
      border-radius: 2px;
    }
  }

  .nav-arrow {
    margin-left: 4px;
    font-size: 12px;
  }
}

/* 有下拉菜单的项：hover 时也显示下划线效果 */
.nav-item.has-dropdown:hover {
  color: #2c3e6b;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 30px;
    height: 3px;
    background: #2c3e6b;
    border-radius: 2px;
  }
}

/* 下拉菜单 */
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  min-width: 160px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  padding: 8px 0;
  z-index: 1001;
}

/* 透明连接区，防止鼠标移到下拉菜单时触发 mouseleave */
.dropdown-menu::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 0;
  right: 0;
  height: 8px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  color: #455a64;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;

  &:hover {
    background: #f0f4fa;
    color: #2c3e6b;
  }

  .dropdown-icon {
    margin-right: 8px;
    font-size: 16px;
  }
}

/* 右侧操作区 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.search-box {
  width: 220px;

  :deep(.el-input__wrapper) {
    border-radius: 20px;
  }
}

.auth-btn {
  border-radius: 20px;
  padding: 8px 20px;
}

/* 用户菜单 */
.user-menu {
  position: relative;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.2s;

  &:hover {
    background: #f0f4fa;
  }

  .user-nickname {
    font-size: 14px;
    color: #455a64;
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .user-arrow {
    font-size: 12px;
    color: #90a4ae;
  }
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 160px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  padding: 8px 0;
  z-index: 1001;
}

/* 透明连接区，防止鼠标移到下拉菜单时触发 mouseleave */
.user-dropdown::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 0;
  right: 0;
  height: 8px;
}

.user-dropdown-item {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  color: #455a64;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;

  &:hover {
    background: #f0f4fa;
    color: #2c3e6b;
  }

  .el-icon {
    margin-right: 8px;
  }
}

.dropdown-divider {
  height: 1px;
  background: #eceff1;
  margin: 6px 0;
}

/* 移动端汉堡按钮 */
.mobile-menu-btn {
  display: none;
  cursor: pointer;
  color: #2c3e6b;
}

/* 移动端导航 */
.mobile-nav {
  display: none;
  background: #fff;
  border-top: 1px solid #eceff1;
  padding: 10px 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.mobile-nav-item {
  display: block;
  padding: 12px 0;
  color: #455a64;
  text-decoration: none;
  font-size: 15px;
  border-bottom: 1px solid #f5f7fa;
}

.mobile-nav-sub {
  padding: 8px 0;

  .mobile-nav-title {
    font-size: 13px;
    color: #90a4ae;
    padding: 8px 0 4px;
  }

  .mobile-nav-child {
    display: block;
    padding: 8px 0 8px 16px;
    color: #607d8b;
    text-decoration: none;
    font-size: 14px;
  }
}

/* 响应式 */
@media (max-width: 992px) {
  .header-nav {
    display: none;
  }

  .search-box {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
  }

  .mobile-nav {
    display: block;
  }

  .auth-btn {
    padding: 6px 14px;
    font-size: 13px;
  }
}

@media (max-width: 576px) {
  .site-name {
    font-size: 16px !important;
  }

  .user-nickname {
    display: none;
  }

  .auth-btn {
    display: none;
  }
}
</style>
