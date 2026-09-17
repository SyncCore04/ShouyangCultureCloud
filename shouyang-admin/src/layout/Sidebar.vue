<template>
  <aside class="sidebar" :class="{ collapsed: collapse }">
    <!-- Logo -->
    <div class="logo">
      <el-icon :size="24" color="#fff"><Picture /></el-icon>
      <span v-if="!collapse" class="logo-text">寿阳文旅云</span>
    </div>

    <!-- 菜单 -->
    <el-menu
      :default-active="activeMenu"
      :collapse="collapse"
      :collapse-transition="false"
      background-color="#1a2a4a"
      text-color="rgba(255,255,255,0.75)"
      active-text-color="#fff"
      router
      class="sidebar-menu"
    >
      <template v-for="group in menuGroups" :key="group.title">
        <!-- 分组标题（非折叠时显示） -->
        <div v-if="!collapse" class="menu-group-title">{{ group.title }}</div>

        <el-menu-item
          v-for="item in group.items"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

defineProps({
  collapse: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const activeMenu = computed(() => route.path)

// 菜单分组配置
const menuGroups = [
  {
    title: '概览',
    items: [
      { path: '/dashboard', title: '仪表盘', icon: 'Odometer' }
    ]
  },
  {
    title: '内容管理',
    items: [
      { path: '/banner', title: '轮播图管理', icon: 'Picture' },
      { path: '/news', title: '资讯管理', icon: 'Document' },
      { path: '/category', title: '分类管理', icon: 'Menu' }
    ]
  },
  {
    title: '文旅服务',
    items: [
      { path: '/ticket', title: '票务管理', icon: 'Ticket' },
      { path: '/venue', title: '场馆管理', icon: 'OfficeBuilding' },
      { path: '/activity', title: '活动管理', icon: 'Calendar' }
    ]
  },
  {
    title: '文旅时空',
    items: [
      { path: '/pavilion', title: '展馆管理', icon: 'PictureFilled' },
      { path: '/heritage', title: '非遗管理', icon: 'Star' },
      { path: '/product', title: '文创管理', icon: 'Goods' }
    ]
  },
  {
    title: '畅游寿阳',
    items: [
      { path: '/scenic', title: '景点管理', icon: 'Location' },
      { path: '/food', title: '美食管理', icon: 'Food' },
      { path: '/hotel', title: '酒店管理', icon: 'HomeFilled' },
      { path: '/guide', title: '攻略管理', icon: 'Guide' }
    ]
  },
  {
    title: '系统管理',
    items: [
      { path: '/org', title: '单位管理', icon: 'Building' },
      { path: '/user', title: '用户管理', icon: 'User' },
      { path: '/profile', title: '个人设置', icon: 'Setting' }
    ]
  }
]
</script>

<style lang="scss" scoped>
.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1a2a4a 0%, #0f1a2e 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  overflow: hidden;
  flex-shrink: 0;

  &.collapsed {
    width: 64px;
  }
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;

  .logo-text {
    color: #fff;
    font-size: 16px;
    font-weight: bold;
    letter-spacing: 1px;
    white-space: nowrap;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 4px;
  }

  :deep(.el-menu-item) {
    height: 48px;
    line-height: 48px;
    margin: 2px 8px;
    border-radius: 6px;

    &:hover {
      background: rgba(255, 255, 255, 0.08);
    }

    &.is-active {
      background: rgba(74, 111, 165, 0.3);
    }
  }
}

.menu-group-title {
  padding: 12px 20px 6px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.35);
  letter-spacing: 1px;
  text-transform: uppercase;
}
</style>
