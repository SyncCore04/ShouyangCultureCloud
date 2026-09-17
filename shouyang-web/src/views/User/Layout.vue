<template>
  <div class="user-center-layout">
    <div class="page-container">
      <div class="layout-wrapper">
        <!-- 左侧菜单 -->
        <div class="sidebar">
          <div class="user-card">
            <el-avatar :size="64" :src="userStore.userInfo?.avatar || defaultAvatar" class="user-avatar" />
            <div class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</div>
            <div class="user-id">ID: {{ userStore.userInfo?.id || '-' }}</div>
          </div>
          <el-menu
            :default-active="activeMenu"
            router
            class="side-menu"
            background-color="#fff"
            text-color="#607d8b"
            active-text-color="#2c3e6b"
          >
            <el-menu-item index="/user/profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="/user/favorite">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="/user/activity">
              <el-icon><Tickets /></el-icon>
              <span>我的报名</span>
            </el-menu-item>
            <el-menu-item index="/user/password">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧内容区 -->
        <div class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, Star, Tickets, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const defaultAvatar = 'https://picsum.photos/seed/default-avatar/128/128'

const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.user-center-layout {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 24px 20px; }
.layout-wrapper { display: flex; gap: 24px; align-items: flex-start; }

/* 左侧菜单 */
.sidebar { width: 240px; flex-shrink: 0; position: sticky; top: 20px; }
.user-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.user-avatar { margin-bottom: 12px; border: 3px solid #e8eef7; }
.user-name { font-size: 16px; font-weight: 600; color: #1a2a4a; margin-bottom: 4px; }
.user-id { font-size: 12px; color: #90a4ae; }

.side-menu {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  :deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;
    font-size: 14px;
    border-left: 3px solid transparent;
    &.is-active {
      background: #f0f4fa;
      border-left-color: #2c3e6b;
      font-weight: 600;
    }
    &:hover { background: #f5f8fc; }
  }
}

/* 右侧内容 */
.main-content { flex: 1; min-width: 0; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media (max-width: 768px) {
  .layout-wrapper { flex-direction: column; }
  .sidebar { width: 100%; position: static; }
  .side-menu { display: flex; overflow-x: auto; }
}
</style>
