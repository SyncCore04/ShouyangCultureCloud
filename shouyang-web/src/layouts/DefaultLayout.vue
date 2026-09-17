<template>
  <div class="default-layout">
    <!-- 顶部导航栏 -->
    <Header />

    <!-- 主内容区 -->
    <main class="layout-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
</script>

<style lang="scss" scoped>
.default-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f5f7fa;
}

/* 主内容区：撑满剩余高度，顶部留出导航栏高度 */
.layout-main {
  flex: 1;
  padding-top: 64px; /* 等于 Header 高度 */
  width: 100%;
}

/* 路由切换过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
