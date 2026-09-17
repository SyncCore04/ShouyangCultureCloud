<template>
  <div class="not-found-page">
    <div class="content">
      <h1 class="error-code">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <p class="error-desc">页面可能已被移除、名称已更改或暂时不可用</p>
      <div class="actions">
        <el-button type="primary" size="large" @click="goHome">
          <el-icon><Home /></el-icon>
          返回首页
        </el-button>
        <el-button size="large" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回上一页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}

function goBack() {
  router.back()
}
</script>

<style lang="scss" scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-page;
  padding: 20px;
}

.content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: $primary-color;
  line-height: 1;
  margin-bottom: 16px;
  text-shadow: 0 4px 20px rgba(44, 62, 107, 0.2);
}

.error-message {
  font-size: 24px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;
}

.error-desc {
  font-size: 14px;
  color: $text-secondary;
  margin-bottom: 32px;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;

  .el-button {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }
}
</style>
