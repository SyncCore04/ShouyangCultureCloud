<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <Sidebar :collapse="isCollapse" />

    <!-- 主内容区 -->
    <div class="main-container" :class="{ collapsed: isCollapse }">
      <Header @toggle-collapse="toggleCollapse" :collapse="isCollapse" />
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const isCollapse = ref(false)

function toggleCollapse() {
  isCollapse.value = !isCollapse.value
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  overflow: hidden;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: margin-left 0.3s;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background: #f0f2f5;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
