<template>
  <header class="site-header" :class="{ scrolled: isScrolled }">
    <div class="header-inner container">
      <!-- Logo -->
      <div class="logo" @click="goHome">
        <el-icon :size="28" color="#fff"><Picture /></el-icon>
        <span class="logo-text">寿阳文旅云</span>
      </div>

      <!-- 主导航 -->
      <nav class="main-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item) }"
        >
          {{ item.name }}
          <!-- 下拉菜单 -->
          <template v-if="item.children">
            <ul class="dropdown">
              <li v-for="child in item.children" :key="child.path">
                <router-link :to="child.path">{{ child.name }}</router-link>
              </li>
            </ul>
          </template>
        </router-link>
      </nav>

      <!-- 右侧操作区 -->
      <div class="header-right">
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文旅资讯..."
            size="default"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 用户区 -->
        <div class="user-area">
          <template v-if="userStore.isLogin">
            <el-dropdown trigger="hover" @command="handleUserCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="userStore.avatar">
                  {{ userStore.nickname.charAt(0) }}
                </el-avatar>
                <span class="username">{{ userStore.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="favorite">我的收藏</el-dropdown-item>
                  <el-dropdown-item command="activity">我的报名</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="auth-link">登录</router-link>
            <span class="divider">|</span>
            <router-link to="/register" class="auth-link">注册</router-link>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchKeyword = ref('')
const isScrolled = ref(false)

// 导航菜单配置
const navItems = [
  { name: '首页', path: '/' },
  { name: '文旅动态', path: '/news' },
  {
    name: '文旅服务',
    path: '/service/ticket',
    children: [
      { name: '票务预订', path: '/service/ticket' },
      { name: '场馆预订', path: '/service/venue' },
      { name: '活动报名', path: '/service/activity' },
      { name: '文旅日历', path: '/service/calendar' }
    ]
  },
  {
    name: '文旅时空',
    path: '/culture/pavilion',
    children: [
      { name: '数字展馆', path: '/culture/pavilion' },
      { name: '非遗文化', path: '/culture/heritage' },
      { name: '文创商城', path: '/culture/product' }
    ]
  },
  {
    name: '畅游寿阳',
    path: '/travel/scenic',
    children: [
      { name: '景点推荐', path: '/travel/scenic' },
      { name: '特色美食', path: '/travel/food' },
      { name: '民宿酒店', path: '/travel/hotel' },
      { name: '旅游攻略', path: '/travel/guide' }
    ]
  },
  { name: '文旅单位', path: '/org' }
]

// 判断导航项是否激活
function isActive(item) {
  if (item.children) {
    return item.children.some((child) => route.path.startsWith(child.path.split('/').slice(0, 3).join('/')))
  }
  if (item.path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(item.path)
}

// 搜索
function handleSearch() {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  router.push({ path: '/search', query: { keyword: searchKeyword.value } })
}

// 跳转首页
function goHome() {
  router.push('/')
}

// 用户下拉菜单操作
function handleUserCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/user')
      break
    case 'favorite':
      router.push('/user/favorite')
      break
    case 'activity':
      router.push('/user/activity')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/')
      }).catch(() => {})
      break
  }
}

// 滚动监听
function handleScroll() {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
.site-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: linear-gradient(135deg, $primary-dark 0%, $primary-color 100%);
  box-shadow: $shadow-md;
  transition: all $transition-base;

  &.scrolled {
    box-shadow: $shadow-lg;
  }
}

.header-inner {
  display: flex;
  align-items: center;
  height: 64px;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;

  .logo-text {
    font-size: 20px;
    font-weight: bold;
    color: #fff;
    letter-spacing: 2px;
  }
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-item {
  position: relative;
  padding: 0 16px;
  height: 64px;
  line-height: 64px;
  color: rgba(255, 255, 255, 0.85);
  font-size: 15px;
  cursor: pointer;
  transition: all $transition-fast;
  text-decoration: none;

  &:hover,
  &.active {
    color: #fff;
    background: rgba(255, 255, 255, 0.1);
  }

  &.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 30px;
    height: 3px;
    background: #fff;
    border-radius: 2px;
  }

  // 下拉菜单
  .dropdown {
    position: absolute;
    top: 100%;
    left: 50%;
    transform: translateX(-50%);
    min-width: 140px;
    background: #fff;
    border-radius: 0 0 $border-radius $border-radius;
    box-shadow: $shadow-lg;
    opacity: 0;
    visibility: hidden;
    transform: translateX(-50%) translateY(-10px);
    transition: all $transition-fast;
    z-index: 1001;

    li {
      a {
        display: block;
        padding: 10px 20px;
        color: $text-regular;
        font-size: 14px;
        white-space: nowrap;

        &:hover {
          background: $primary-bg;
          color: $primary-color;
        }
      }
    }
  }

  &:hover .dropdown {
    opacity: 1;
    visibility: visible;
    transform: translateX(-50%) translateY(0);
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.search-box {
  width: 220px;

  :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.15);
    box-shadow: none;
    border-radius: 20px;

    &:hover {
      background: rgba(255, 255, 255, 0.25);
    }

    .el-input__inner {
      color: #fff;

      &::placeholder {
        color: rgba(255, 255, 255, 0.6);
      }
    }

    .el-input__prefix {
      color: rgba(255, 255, 255, 0.7);
    }
  }
}

.user-area {
  display: flex;
  align-items: center;
}

.auth-link {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  cursor: pointer;

  &:hover {
    color: #fff;
  }
}

.divider {
  color: rgba(255, 255, 255, 0.4);
  margin: 0 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background $transition-fast;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }

  .username {
    color: #fff;
    font-size: 14px;
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .el-icon {
    color: rgba(255, 255, 255, 0.7);
    font-size: 12px;
  }
}
</style>
