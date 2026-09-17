<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
  </div>
</template>

<script setup>
// 404页面
</script>

<style lang="scss" scoped>
.not-found {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;

  h1 {
    font-size: 100px;
    color: #2c3e6b;
    margin: 0;
  }

  p {
    font-size: 18px;
    color: #909399;
    margin: 16px 0 24px;
  }
}
</style>
